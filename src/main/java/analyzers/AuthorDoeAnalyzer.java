package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoeDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import utils.ModelDOE;
import utils.RepositoryAnalyzer;

public class AuthorDoeAnalyzer extends AnalyzerGeneric {
	
	public AuthorDoeAnalyzer(List<File> files) {
		super();
		this.files = files;
	}
	
	public void runDOEAnalysis() throws GitAPIException {
		ContributorDAO authorDao = new ContributorDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoeDAO authorDoeDAO = new AuthorDoeDAO();
		ModelDOE modelDOE = new ModelDOE();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		Commit lastCommit = RepositoryAnalyzer.getLastCommit();
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorDoeDAO.existsByAuthorVersion(authorFile, lastCommit) == false) {
						AuthorDOE authorDOE = new AuthorDOE(authorFile, lastCommit, 
								modelDOE.getContributorFileDOE(contributor, file));
						authorDoeDAO.persist(authorDOE);
					}
				}
			}
		}
	}
}
