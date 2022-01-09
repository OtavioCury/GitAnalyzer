package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;

import dao.AuthorBlameDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.FileVersionDAO;
import model.AuthorBlame;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.FileVersion;
import model.Project;
import utils.ContributorsUtils;
import utils.RepositoryAnalyzer;

public class AuthorBlameAnalyzer {

	private Project project;

	public AuthorBlameAnalyzer(Project project) {
		super();
		this.project = project;
	}

	public void runBlameAnalysis() {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorBlameDAO authorBlameDao = new AuthorBlameDAO();
		FileVersionDAO FileVersionDAO = new FileVersionDAO();
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorBlameDao.existsByAuthorVersion(authorFile, currentCommit) == false
							&& FileVersionDAO.existsByFileVersion(file, currentCommit)) {
						try {
							FileVersion FileVersion = FileVersionDAO.findByFileVersion(file, currentCommit); 
							AuthorBlame authorBlame = authorBlameDao.findByAuthorVersion(authorFile, currentCommit);
							int blame = 0;
							BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
							blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
							blameCommand.setFilePath(file.getPath());
							BlameResult blameResult = blameCommand.call();
							for (int i = 0; i < FileVersion.getNumberLines(); i++) {
								PersonIdent autor = blameResult.getSourceAuthor(i);
								if (autor.getName().equals(contributor.getName())) {
									blame++;
								}
							}
							authorBlame = new AuthorBlame(authorFile, currentCommit, blame);
							authorBlameDao.persist(authorBlame);
						} catch (GitAPIException | java.lang.NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
