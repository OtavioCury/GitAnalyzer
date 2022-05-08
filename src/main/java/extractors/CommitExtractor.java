package extractors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Iterable<RevCommit> commits = RepositoryAnalyzer.git.log().setRevFilter(RevFilter.NO_MERGES).call();
		List<RevCommit> commitsList = new ArrayList<RevCommit>();
		commits.forEach(commitsList::add);
		Collections.sort(commitsList, new Comparator<RevCommit>() {
			public int compare(RevCommit commit1, RevCommit commit2) {
				if (commit1.getAuthorIdent().getWhen().after(commit2.getAuthorIdent().getWhen())) {
					return -1;
				}else if(commit1.getAuthorIdent().getWhen().before(commit2.getAuthorIdent().getWhen())) {
					return 1;
				}else {
					return 0;
				}
			}
		});
		//		List<String> invalidsCommits = project.getProjectConstants() != null ? null: 
		//			project.getProjectConstants().getInvalidCommits();
		for (int i = 0; i < commitsList.size(); i++) {//analyze each commit
			if(commitDao.findByIdExistsByProject(commitsList.get(i).getName(), project) == false) {
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
				Contributor author = contributorDao.findByNameEmailProject(authorName, authorEmail, project);
				if(author == null) {
					author = new Contributor(authorName, authorEmail, project);
					contributorDao.persist(author);
				}
				Contributor committer = contributorDao.findByNameEmailProject(committerName, committerEmail, project);
				if(committer == null) {
					committer = new Contributor(committerName, committerEmail, project);
					contributorDao.persist(committer);
				}
				List<String> parents = new ArrayList<String>();
				for(RevCommit parent: commitsList.get(i).getParents()) {
					parents.add(parent.getName());
				}
				Commit commit = new Commit(author, committer, project, commitsList.get(i).getAuthorIdent().getWhen(), 
						commitsList.get(i).getName(), parents);
				commitDao.persist(commit);
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
					if(commitFileDao.existsByCommitFile(commit.getExternalId(), file.getPath()) == false) {
						CommitFile commitFile = new CommitFile();
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
			System.out.println(i);
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
		int adds = 0;
		HashMap<String, Integer> modifications = new HashMap<String, Integer>();
		if(fileDiff !=null ){
			String[] lines = fileDiff.split("\n");

			for(int i = 0; i < lines.length; i++){
				if((i > 3) && (lines[i].length() > 0)){
					if((lines[i].charAt(0) == '+') && (lines[i].substring(1).trim().length() > 0)) {
						adds++;
					}
				}
			}
		}
		modifications.put("adds", adds);
		return modifications;
	}

	private static boolean isSimilar(String string1, String string2){
		int result = Levenshtein.distance(string1, string2);
		if(((double)result/string1.length()) < 0.4)
			return true;
		return false;

	}

}
