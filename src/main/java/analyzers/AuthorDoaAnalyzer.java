package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorDoaDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.FileCommitDAO;
import model.AuthorDOA;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.FileUtils;
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
		FileCommitDAO fileCommitDAO = new FileCommitDAO();
		List<Contributor> contributors = ContributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils modelDOA = new DoaUtils(currentCommit);
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(fileCommitDAO.existsByFileCommit(file, currentCommit) == true && 
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
