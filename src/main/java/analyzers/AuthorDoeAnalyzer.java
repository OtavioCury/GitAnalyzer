package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoeDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.FileUtils;
import utils.ModelDOE;
import utils.RepositoryAnalyzer;

public class AuthorDoeAnalyzer extends AnalyzerGeneric {
	
	public AuthorDoeAnalyzer(Project project) {
		super();
		this.project = project;
	}
	
	public void runDOEAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoeDAO authorDoeDAO = new AuthorDoeDAO();
		List<Contributor> contributors = ContributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		ModelDOE modelDOE = new ModelDOE(currentCommit);
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorDoeDAO.existsByAuthorVersion(authorFile, currentCommit) == false) {
						AuthorDOE authorDOE = new AuthorDOE(authorFile, currentCommit, 
								modelDOE.getContributorFileDOE(contributor, file));
						authorDoeDAO.persist(authorDOE);
					}
				}
			}
		}
	}
}
