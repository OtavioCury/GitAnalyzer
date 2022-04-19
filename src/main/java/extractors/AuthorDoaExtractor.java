package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoaDAO;
import dao.AuthorFileDAO;
import dao.ContributorDAO;
import model.AuthorDOA;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoaExtractor {

	private Project project;

	public AuthorDoaExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runDOAAnalysis() throws GitAPIException {
		DoaUtils doaUtils = new DoaUtils();
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoaDAO authorDoaDAO = new AuthorDoaDAO();
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
				if(authorFile != null && 
						authorDoaDAO.existsByAuthorVersion(authorFile, currentCommit) == false) {
					AuthorDOA authorDOA = new AuthorDOA(authorFile, currentCommit, 
							doaUtils.getContributorFileDOA(contributor, file));
					authorDoaDAO.persist(authorDOA);
				}
			}
		}
	}
}
