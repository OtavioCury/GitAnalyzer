package analyzers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import dao.ProjectDAO;
import model.Contributor;
import model.Project;
import utils.Constants;
import utils.ModelDOE;
import utils.RepositoryAnalyzer;

public class Analyzer {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(Constants.projectName);
		if(project == null) {
			project = new Project(Constants.projectName);
			projectDao.persist(project);
		}
		RepositoryAnalyzer.git = Git.open(new File(Constants.fullPath));
		RepositoryAnalyzer.repository = RepositoryAnalyzer.git.getRepository();
		analyzeCommits(project);
		analyzeContributors(project);
		analyzeFiles(project);
		analyzeMetrics(project);
//		getMantainers(files);
		RepositoryAnalyzer.git.close();
	}
	
	private static void analyzeContributors(Project project) {
		ContributorAnalyzer contributorAnalyzer = new ContributorAnalyzer(project);
		contributorAnalyzer.run();
	}

	private static void getMantainers(List<model.File> files) {
		ModelDOE modelDOE = new ModelDOE(RepositoryAnalyzer.getCurrentCommit());
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
	
	private static void analyzeMetrics(Project project) {
		AuthorFileAnalyzer authorFileAnalyzer = new AuthorFileAnalyzer(project);
		try {
			authorFileAnalyzer.runFirstAuthorAnalysis();
		} catch (GitAPIException e1) {
			e1.printStackTrace();
		}
		AuthorBlameAnalyzer authorBlameAnalyzer = new AuthorBlameAnalyzer(project);
		authorBlameAnalyzer.runBlameAnalysis();
		AuthorDoeAnalyzer authorDoeAnalyzer = new AuthorDoeAnalyzer(project);
		try {
			
			authorDoeAnalyzer.runDOEAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}		
	}

}
