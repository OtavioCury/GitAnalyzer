package extractors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.xmlbeans.impl.common.Levenshtein;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import dao.CommitDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import dao.FileDAO;
import dao.FileRenameDAO;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.File;
import model.FileRename;
import model.Project;
import utils.Constants;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class CommitExtractor {

	private Project project;

	public CommitExtractor(Project project) {
		super();
		this.project = project;
	}

	public void run() throws NoHeadException, GitAPIException, IOException {
		ContributorDAO contributorDao = new ContributorDAO();
		CommitDAO commitDao = new CommitDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		FileDAO fileDAO = new FileDAO();
		FileRenameDAO fileRenameDAO = new FileRenameDAO();
		Iterable<RevCommit> commits = RepositoryAnalyzer.git.log().setRevFilter(RevFilter.NO_MERGES).all().call();
		List<RevCommit> commitsList = new ArrayList<RevCommit>();
		commits.forEach(commitsList::add);
		Date analisysDate = Constants.analisysDate();
		List<String> invalidsCommits = project.getProjectConstants().getInvalidCommits();
		for (int i = 0; i < commitsList.size(); i++) {//analyze each commit
			if(commitDao.findByIdExists(commitsList.get(i).getName()) == false
					&& invalidsCommits.contains(commitsList.get(i).getName()) == false) {
				if(commitsList.get(i).getAuthorIdent().getWhen().after(analisysDate)) {
					String authorName = null, authorEmail = null;
					if (commitsList.get(i).getAuthorIdent() != null) {
						if (commitsList.get(i).getAuthorIdent().getEmailAddress() != null) {
							authorEmail = commitsList.get(i).getAuthorIdent().getEmailAddress();
						}
						if (commitsList.get(i).getAuthorIdent().getName() != null) {
							authorName = commitsList.get(i).getAuthorIdent().getName();
						}
					}
					String committerName = null, committerEmail = null;
					if (commitsList.get(i).getCommitterIdent() != null){
						if (commitsList.get(i).getAuthorIdent().getEmailAddress() != null) {
							committerEmail = commitsList.get(i).getCommitterIdent().getEmailAddress();
						}
						if (commitsList.get(i).getAuthorIdent().getName() != null) {
							committerName = commitsList.get(i).getCommitterIdent().getName();
						}
					}
					Contributor author = contributorDao.findByNameEmail(authorName, authorEmail);
					if(author == null) {
						author = new Contributor(authorName, authorEmail, project);
						contributorDao.persist(author);
					}
					Contributor committer = contributorDao.findByNameEmail(committerName, committerEmail);
					if(committer == null) {
						committer = new Contributor(committerName, committerEmail, project);
						contributorDao.persist(committer);
					}
					Commit commit = commitDao.findById(commitsList.get(i).getName());
					if(commit == null) {
						List<String> parents = new ArrayList<String>();
						for(RevCommit parent: commitsList.get(i).getParents()) {
							parents.add(parent.getName());
						}
						commit = new Commit(author, committer, commitsList.get(i).getAuthorIdent().getWhen(), 
								commitsList.get(i).getName(), parents);
						commitDao.persist(commit);
					}
					List<DiffEntry> diffsForTheCommit = diffsForTheCommit(RepositoryAnalyzer.repository, commitsList.get(i));
					for (DiffEntry diff : diffsForTheCommit) {
						String newPath = diff.getNewPath().toString();
						String oldPath = diff.getOldPath().toString();

						File newFile = fileDAO.findByPath(newPath, project);
						if(Constants.invalidPaths.contains(newPath) == false) {
							if(newFile == null) {
								newFile = new model.File(newPath, project, FileUtils.returnFileExtension(newPath));
								fileDAO.persist(newFile);
							}
						}

						File oldFile = fileDAO.findByPath(oldPath, project);
						if(Constants.invalidPaths.contains(oldPath) == false) {
							if(oldFile == null) {
								oldFile = new model.File(oldPath, project, FileUtils.returnFileExtension(oldPath));
								fileDAO.persist(oldFile);
							}
						}

						if(newPath.equals(oldPath) == false && newFile != null && oldFile != null) {
							FileRename fileRename = new FileRename(oldFile, newFile, commit);
							fileRenameDAO.persist(fileRename);
						}

						File file = null;
						if(newFile != null) {
							file = newFile;
						}else {
							file = oldFile;
						}
						CommitFile commitFile = commitFileDao.findByCommitFile(commit.getExternalId(), file.getPath());
						if(commitFile == null) {
							commitFile = new CommitFile();
							if(diff.getChangeType().name().equals(Constants.ADD)){
								commitFile.setOperation(enums.OperationType.ADD);
							}else if(diff.getChangeType().name().equals(Constants.DELETE)){
								commitFile.setOperation(enums.OperationType.DEL);
							}else if(diff.getChangeType().name().equals(Constants.MODIFY)){
								commitFile.setOperation(enums.OperationType.MOD);
							}else if(diff.getChangeType().name().equals(Constants.RENAME)) {
								commitFile.setOperation(enums.OperationType.REN);
							}else{
								continue;
							}

							commitFile.setFile(file);

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							DiffFormatter diffFormatter = new DiffFormatter( stream );
							diffFormatter.setRepository(RepositoryAnalyzer.repository);
							try {
								diffFormatter.format(diff);
							} catch (IOException e) {
								e.printStackTrace();
							}

							String in = stream.toString();

							Map<String, Integer> modifications = analyze(in);
							commitFile.setAdds(modifications.get("adds"));
							commitFile.setDels(modifications.get("dels"));
							commitFile.setCommit(commit);
							commitFileDao.persist(commitFile);

							try {
								diffFormatter.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
							diffFormatter.close();
						}
					}
				}
			}
		}
	}

	private static List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException, AmbiguousObjectException, 
	IncorrectObjectTypeException { 
		AnyObjectId currentCommit = repo.resolve(commit.getName()); 
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null; 
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE); 
		df.setBinaryFileThreshold(2 * 1024); //2 MB MAX A FILE
		df.setRepository(repo); 
		df.setDiffComparator(RawTextComparator.DEFAULT); 
		df.setDetectRenames(true); 
		List<DiffEntry> diffs = null; 
		if (parentCommit == null) { 
			RevWalk rw = new RevWalk(repo); 
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree())); 
			rw.close(); 
		} else { 
			diffs = df.scan(parentCommit, currentCommit); 
		} 
		df.close();
		return diffs; 
	}

	private static Map<String, Integer> analyze(String fileDiff){
		Stack<String> additions = new Stack<String>();
		Stack<String> deletions = new Stack<String>();
		int adds = 0, mods = 0, dels = 0, conditions = 0;
		HashMap<String, Integer> modifications = new HashMap<String, Integer>();
		if(fileDiff !=null ){
			String[] lines = fileDiff.split("\n");

			for(int i = 0; i < lines.length; i++){
				if((i > 3) && (lines[i].length() > 0)){
					if((lines[i].charAt(0) == '+') && (lines[i].substring(1).trim().length() > 0)) {
						additions.push(lines[i].substring(1));
					}else if((lines[i].charAt(0) == '-') && (lines[i].substring(1).trim().length() > 0)) {
						deletions.push(lines[i].substring(1));
					}else if ((!additions.isEmpty()) || (!deletions.isEmpty())) {
						for (String temp : additions) {
							if (temp.trim().startsWith("if")) {
								conditions++;
							}
						}
						while((!additions.isEmpty()) || (!deletions.isEmpty())){
							if(additions.isEmpty()){
								deletions.pop();
								dels++;
							} else if(deletions.isEmpty()){
								additions.pop();
								adds++;
							} else {
								String add = additions.pop();
								String del = deletions.pop();
								if(isSimilar(add, del)){
									mods++;
								} else if(additions.size() > deletions.size()){
									deletions.push(del);
									adds++;
								} else {
									additions.push(add);
									dels++;
								}
							}
						}
					}
				}
			}
		}
		if (!additions.isEmpty()) {
			additions.pop();
			adds++;
		}
		if(!deletions.isEmpty()){
			deletions.pop();
			dels++;
		}
		modifications.put("adds", adds);
		modifications.put("mods", mods);
		modifications.put("dels", dels);
		modifications.put("conditions", conditions);
		return modifications;
	}

	private static boolean isSimilar(String string1, String string2){
		int result = Levenshtein.distance(string1, string2);
		if(((double)result/string1.length()) < 0.4)
			return true;
		return false;

	}

}
