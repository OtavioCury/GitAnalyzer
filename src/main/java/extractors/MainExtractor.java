package extractors;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import dao.ProjectDAO;
import model.Project;
import utils.RepositoryAnalyzer;

public class MainExtractor {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		System.out.println("================ Analyzing "+projectName+"============");
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		RepositoryAnalyzer.initRepository(projectName);
		extractCommits(project);
		extractContributors(project);
		extractFiles(project);
		extractAuthorFile(project);
		extractDoe(project);
		extractDoa(project);
		System.out.println("================ End of Analysis ===========");
		RepositoryAnalyzer.git.close();
	}

	private static void extractDoa(Project project) {
		AuthorDoaExtractor authorDoaAnalyzer = new AuthorDoaExtractor(project);
		try {
			authorDoaAnalyzer.runDOAAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}		
	}

	private static void extractDoe(Project project) {
		AuthorDoeExtractor authorDoeAnalyzer = new AuthorDoeExtractor(project);
		try {
			authorDoeAnalyzer.runDOEAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private static void extractAuthorFile(Project project) {
		AuthorFileExtractor authorFileAnalyzer = new AuthorFileExtractor(project);
		try {
			authorFileAnalyzer.runFirstAuthorAnalysis();
		} catch (GitAPIException e1) {
			e1.printStackTrace();
		}		
	}

	private static void extractContributors(Project project) {
		ContributorExtractor contributorAnalyzer = new ContributorExtractor();
		contributorAnalyzer.run();
	}

	private static void extractCommits(Project project) {
		CommitExtractor commitAnalyzer = new CommitExtractor(project);
		try {
			commitAnalyzer.run();
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void extractFiles(Project project) {
		FileExtractor fileAnalyzer = new FileExtractor(project);
		fileAnalyzer.run();
	}

}
