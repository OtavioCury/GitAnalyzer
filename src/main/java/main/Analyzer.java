package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;

import dao.FileDAO;
import dao.ProjectDAO;
import model.Project;
import utils.Constants;
import utils.RepositoryAnalyzer;

public class Analyzer {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectDAO projectDao = new ProjectDAO();
		System.out.println("====== Analyzing projeto IHealth =======");
		Project project = projectDao.findByName(Constants.projectName);
		if(project == null) {
			project = new Project(Constants.projectName);
			projectDao.persist(project);
		}
		RepositoryAnalyzer.git = Git.open(new File(Constants.fullPath));
		RepositoryAnalyzer.repository = RepositoryAnalyzer.git.getRepository();
		List<model.File> files = getFiles(project);
		int size = files.size();
		List<model.File> first = new ArrayList<>(files.subList(0, (size) / 2));
        List<model.File> second = new ArrayList<>(files.subList((size) / 2, size));
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(new CreateBase(first));
        executorService.execute(new CreateBase(second));
        executorService.shutdown();
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
