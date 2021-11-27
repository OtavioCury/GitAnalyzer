package analyzers;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;

import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.Project;
import utils.CommitsUtils;
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
//		analyzeFiles(files);
//		analyzeContributors(files);
//		getMantainers(files);
	}
	
	private static void getMantainers(List<model.File> files) {
		ModelDOE modelDOE = new ModelDOE();
		Commit currentVersion = CommitsUtils.getCurrentVersion();
		for (model.File file : files) {
			List<Contributor> mantainers = modelDOE.getMantainersByFile(currentVersion, file, Constants.thresholdMantainer);
			System.out.println();
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

	private static void analyzeFiles(List<model.File> files) {
		FileAnalyzer fileAnalyzer = new FileAnalyzer(files);
		try {
			fileAnalyzer.run();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	private static void analyzeContributors(List<model.File> files) {
		AuthorFileAnalyzer authorFileAnalyzer = new AuthorFileAnalyzer(files);
		AuthorBlameAnalyzer authorBlameAnalyzer = new AuthorBlameAnalyzer(files);
		AuthorDoeAnalyzer authorDoeAnalyzer = new AuthorDoeAnalyzer(files);
		try {
			authorFileAnalyzer.runFirstAuthorAnalysis();
			authorBlameAnalyzer.runBlameAnalysis();
			authorDoeAnalyzer.runDOEAnalysis();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}		
	}

	private static void setTimerForMap() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				RepositoryAnalyzer.diffsCommits = new HashMap<String, List<DiffEntry>>();
			}
		};
		Calendar today = Calendar.getInstance();
		Timer timer = new Timer();
		timer.schedule(task, today.getTime(), (TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))/2);
	}

}
