package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;

import dao.FileDAO;
import dao.ProjectDAO;
import model.Project;
import utils.Constants;
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
		List<model.File> files = getFiles(project);
		files = files.stream().filter(file -> Constants.analyzedExtensions.contains(file.getExtension()))
				.collect(Collectors.toList());
		analyzeCommits(files);
		analyzeFiles(files);
		analyzeContributors(files);
	}

	private static void analyzeCommits(List<model.File> files) {
		List<model.File> filesNotAnalyzed = files.stream().filter(file -> file.isCommitsAnalyzed() == false)
				.collect(Collectors.toList());
		int size = filesNotAnalyzed.size();
		if(size > 0) {
			List<model.File> first = new ArrayList<>(filesNotAnalyzed.subList(0, (size) / 2));
			List<model.File> second = new ArrayList<>(filesNotAnalyzed.subList((size) / 2, size));
			setTimerForMap();
			ExecutorService executorService = Executors.newFixedThreadPool(2);
			executorService.execute(new CommitAnalyzer(first));
			executorService.execute(new CommitAnalyzer(second));
			executorService.shutdown();
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
		ContributorFileAnalyzer contributorFileAnalyzer = new ContributorFileAnalyzer(files);
		try {
			//contributorFileAnalyzer.runFirstAuthorAnalysis();
			//contributorFileAnalyzer.runBlameAnalysis();
			contributorFileAnalyzer.runDOEAnalysis();
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

	private static List<model.File> getFiles(Project project) {
		FileDAO fileDao = new FileDAO();
		List<model.File> files = new ArrayList<model.File>();
		try{
			FileInputStream fstream = new FileInputStream(Constants.filesFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null){
				model.File file = fileDao.findByPath(strLine, project);
				if(file == null) {
					String extension = strLine.substring(strLine.lastIndexOf("/")+1);
					extension = extension.substring(extension.indexOf(".")+1);
					file = new model.File(strLine, project, extension, false);
					fileDao.persist(file);
				}
				files.add(file);
			}
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return files;
	}

}
