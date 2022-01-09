package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoaDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.FileVersionDAO;
import model.AuthorDOA;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoaAnalyzer {

	private Project project;

	public AuthorDoaAnalyzer(Project project) {
		super();
		this.project = project;
	}

	public void runDOAAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoaDAO authorDoaDAO = new AuthorDoaDAO();
		FileVersionDAO FileVersionDAO = new FileVersionDAO();
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils modelDOA = new DoaUtils(currentCommit);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(FileVersionDAO.existsByFileVersion(file, currentCommit) == true && 
						commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorDoaDAO.existsByAuthorVersion(authorFile, currentCommit) == false) {
						AuthorDOA authorDOA = new AuthorDOA(authorFile, currentCommit, 
								modelDOA.getContributorFileDOA(contributor, file));
						authorDoaDAO.persist(authorDOA);
					}
				}
			}
		}
	}
}
