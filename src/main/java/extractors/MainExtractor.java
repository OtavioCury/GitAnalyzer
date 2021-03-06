package extractors;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

import dao.ProjectDAO;
import model.Project;
import utils.RepositoryAnalyzer;

public class MainExtractor {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectExtractor projectExtractor = new ProjectExtractor();
		projectExtractor.run(args[0]);
		String projectName = projectExtractor.extractProjectName(args[0]);
		System.out.println("================ Extracting "+projectName+"============");
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		RepositoryAnalyzer.initRepository(projectName);
		System.out.println("=========== Extracting files =======");
		//		extractFiles(project);
		System.out.println("=========== Extracting commits =======");
		//		extractCommits(project);
		System.out.println("=========== Extracting contributors =======");
		//		extractContributors(project);
		//		extractSquads(project);
		System.out.println("=========== Extracting files versions =======");
		//		extractFilesVersion(project);
		System.out.println("=========== Extracting author files =======");
		//		extractAuthorFile(project);
		System.out.println("=========== Extracting files references =======");
		//		extractFileGraph(project);
		System.out.println("=========== Extracting DOEs =======");
		//		extractDoe(project);
		System.out.println("=========== Extracting DOAs =======");
		//		extractDoa(project);
		System.out.println("================ End of Analysis ===========");
		RepositoryAnalyzer.git.close();
	}

	private static void extractSquads(Project project) {
		SquadExtractor squadExtractor = new SquadExtractor(project);
		squadExtractor.run();
	}

	private static void extractFilesVersion(Project project) {
		FileVersionExtractor fileVersionAnalyzer = new FileVersionExtractor(project);
		fileVersionAnalyzer.run();
	}

	private static void extractFileGraph(Project project) {
		FileGraphExtractor fileGraphExtractor = new FileGraphExtractor(project);
		fileGraphExtractor.runExtractor();
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
		ContributorExtractor contributorAnalyzer = new ContributorExtractor(project);
		contributorAnalyzer.run();
	}

	private static void extractCommits(Project project) {
		CommitExtractor commitAnalyzer = new CommitExtractor();
		try {
			commitAnalyzer.extractCommitsWithPersistence(project);
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
		}
	}

	private static void extractFiles(Project project) {
		FileExtractor fileAnalyzer = new FileExtractor(project);
		fileAnalyzer.extractFromRepository(RepositoryAnalyzer.repository);
	}

}
