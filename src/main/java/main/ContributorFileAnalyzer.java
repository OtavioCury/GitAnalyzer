package main;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;

import dao.AuthorBlameDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import model.AuthorBlame;
import model.AuthorFile;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.File;
import utils.CommitsUtils;
import utils.RepositoryAnalyzer;

public class ContributorFileAnalyzer {

	private List<model.File> files;

	public ContributorFileAnalyzer(List<File> files) {
		super();
		this.files = files;
	}

	public void run() throws GitAPIException {
		ContributorDAO authorDao = new ContributorDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorBlameDAO authorBlameDao = new AuthorBlameDAO();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				CommitFile commitFile = commitFileDao.findByAuthorFileAdd(contributor, file);
				AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
				if(authorFile == null) {
					boolean firstAuthor = false;
					if(commitFile != null) {
						firstAuthor = true;
					}
					authorFile = new AuthorFile(contributor, file, firstAuthor);
					authorFileDao.persist(authorFile);
				}
				Commit lastCommit = CommitsUtils.getCurrentVersion();
				AuthorBlame authorBlame = authorBlameDao.findByAuthorVersion(authorFile, lastCommit);
				if(authorBlame == null) {
					int blame = 0;
					BlameResult blameResult = null;
					if(utils.FileAnalyzer.blameResultsFile.containsKey(file.getPath()) == false) {
						BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
						blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
						blameCommand.setFilePath(file.getPath());
						blameResult = blameCommand.call();
						utils.FileAnalyzer.blameResultsFile.put(file.getPath(), blameResult);
					}else {
						blameResult = utils.FileAnalyzer.blameResultsFile.get(file.getPath());
					}
					for (int i = 0; i < file.getNumberLines(); i++) {
						PersonIdent autor = blameResult.getSourceAuthor(i);
						if (autor.getName().equals(contributor.getName())) {
							blame++;
						}
					}
					authorBlame = new AuthorBlame(authorFile, lastCommit, blame);
					authorBlameDao.persist(authorBlame);
				}
			}
		}
	}

}
