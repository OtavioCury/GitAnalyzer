package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoeDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import dao.FileVersionDAO;
import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoeExtractor {

	private Project project;

	public AuthorDoeExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runDOEAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoeDAO authorDoeDAO = new AuthorDoeDAO();
		FileVersionDAO FileVersionDAO = new FileVersionDAO();
		ContributorDAO contributorDAO = new ContributorDAO();
		List<Contributor> contributors = contributorDAO.findByProject(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(FileVersionDAO.existsByFileVersion(file, currentCommit) == true &&
						commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorDoeDAO.existsByAuthorVersion(authorFile, currentCommit) == false) {
						AuthorDOE authorDOE = new AuthorDOE(authorFile, currentCommit, 
								DoeUtils.getContributorFileDOE(contributor, file));
						authorDoeDAO.persist(authorDOE);
					}
				}
			}
		}
	}
}
