package utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import dao.CommitDAO;
import dao.ProjectDAO;
import model.Commit;
import model.Project;

public class RepositoryAnalyzer {

	public static Git git;
	public static Repository repository;
	public static HashMap<String, List<DiffEntry>> diffsCommits;
	private static List<model.File> analyzedFiles;

	public static Commit getCurrentCommit() {
		CommitDAO commitDao = new CommitDAO();
		return commitDao.findLastCommit();
	}

	public static void initRepository(String projectName) {
		ProjectDAO projectDAO = new ProjectDAO();
		Project project = projectDAO.findByName(projectName);
		try {
			RepositoryAnalyzer.git = Git.open(new File(project.getCurrentPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		RepositoryAnalyzer.repository = RepositoryAnalyzer.git.getRepository();
	}
	
	public static void closeRepository() {
		RepositoryAnalyzer.git.close();
	}

	public static List<model.File> getAnalyzedFiles(Project project) {
		if(analyzedFiles == null) {
			setAnalyzedFiles(FileUtils.filesToBeAnalyzed(project));
		}
		return analyzedFiles;
	}

	public static void setAnalyzedFiles(List<model.File> analyzedFiles) {
		RepositoryAnalyzer.analyzedFiles = analyzedFiles;
	}

}
