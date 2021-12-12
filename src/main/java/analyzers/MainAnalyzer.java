package analyzers;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import dao.ProjectDAO;
import model.Contributor;
import model.Project;
import utils.Constants;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;

public class MainAnalyzer {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectInitializer.init(args[0]);
		String projectName = ProjectInitializer.extractProjectName(args[0]);
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		RepositoryAnalyzer.initRepository(projectName);
		analyzeCommits(project);
		analyzeContributors(project);
		analyzeFiles(project);
		analyzeAuthorFile(project);
		analyzeDoe(project);
		analyzeDoa(project);
		RepositoryAnalyzer.git.close();
	}

	private static void analyzeDoa(Project project) {
		AuthorDoaAnalyzer authorDoaAnalyzer = new AuthorDoaAnalyzer(project);
		try {
			authorDoaAnalyzer.runDOAAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}		
	}

	private static void analyzeDoe(Project project) {
		AuthorDoeAnalyzer authorDoeAnalyzer = new AuthorDoeAnalyzer(project);
		try {
			authorDoeAnalyzer.runDOEAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private static void analyzeAuthorFile(Project project) {
		AuthorFileAnalyzer authorFileAnalyzer = new AuthorFileAnalyzer(project);
		try {
			authorFileAnalyzer.runFirstAuthorAnalysis();
		} catch (GitAPIException e1) {
			e1.printStackTrace();
		}		
	}

	private static void analyzeContributors(Project project) {
		ContributorAnalyzer contributorAnalyzer = new ContributorAnalyzer(project);
		contributorAnalyzer.run();
	}

	private static void getMantainers(List<model.File> files) {
		DoeUtils modelDOE = new DoeUtils(RepositoryAnalyzer.getCurrentCommit());
		for (model.File file : files) {
			List<Contributor> mantainers = modelDOE.getMantainersByFile(file, Constants.thresholdMantainer);
		}
	}

	private static void analyzeCommits(Project project) {
		CommitAnalyzer commitAnalyzer = new CommitAnalyzer(project);
		try {
			commitAnalyzer.run();
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void analyzeFiles(Project project) {
		FileAnalyzer fileAnalyzer = new FileAnalyzer(project);
		fileAnalyzer.run();
	}

}
