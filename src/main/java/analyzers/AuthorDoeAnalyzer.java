package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoeDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.FileCommitDAO;
import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.DoeUtils;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class AuthorDoeAnalyzer {
	
	private Project project;
	
	public AuthorDoeAnalyzer(Project project) {
		super();
		this.project = project;
	}
	
	public void runDOEAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorDoeDAO authorDoeDAO = new AuthorDoeDAO();
		FileCommitDAO fileCommitDAO = new FileCommitDAO();
		List<Contributor> contributors = ContributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils modelDOE = new DoeUtils(currentCommit);
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(fileCommitDAO.existsByFileCommit(file, currentCommit) == true &&
						commitFileDao.existsByAuthorFile(contributor, file) == true) {
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
