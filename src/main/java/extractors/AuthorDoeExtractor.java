package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoeDAO;
import dao.AuthorFileDAO;
import dao.ContributorDAO;
import dao.FileVersionDAO;
import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoeExtractor {

	private Project project;
	private AuthorFileDAO authorFileDao = new AuthorFileDAO();

	public AuthorDoeExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runDOEAnalysis() throws GitAPIException {
		DoeUtils doeUtils = new DoeUtils();
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		AuthorDoeDAO authorDoeDAO = new AuthorDoeDAO();
		FileVersionDAO fileVersionDAO = new FileVersionDAO();
		ContributorDAO contributorDAO = new ContributorDAO();
		List<Contributor> contributors = contributorDAO.findByProjectDevs(project);
		for (Contributor contributor : contributors) {
			contributorsUtils.setAlias(contributor);
		}
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
				if(authorFile != null && authorDoeDAO.existsByAuthorVersion(authorFile, currentCommit) == false 
						&& fileVersionDAO.existsByFileVersionNumberLines(file, currentCommit) == true) {
					AuthorDOE authorDOE = new AuthorDOE(authorFile, currentCommit, 
							doeUtils.getContributorFileDOE(contributor, file));
					authorDoeDAO.persist(authorDOE);
				}
			}
		}
	}
}
