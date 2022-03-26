package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoaDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import dao.FileVersionDAO;
import model.AuthorDOA;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.DoaUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoaExtractor {

	private Project project;

	public AuthorDoaExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runDOAAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoaDAO authorDoaDAO = new AuthorDoaDAO();
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
					if(authorDoaDAO.existsByAuthorVersion(authorFile, currentCommit) == false) {
						AuthorDOA authorDOA = new AuthorDOA(authorFile, currentCommit, 
								DoaUtils.getContributorFileDOA(contributor, file));
						authorDoaDAO.persist(authorDOA);
					}
				}
			}
		}
	}
}
