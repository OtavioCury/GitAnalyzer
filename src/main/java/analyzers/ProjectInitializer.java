package analyzers;

import java.io.File;

import dao.ProjectDAO;
import model.Project;

public class ProjectInitializer {

	public static void init(String projectPath) {
		String projectName = extractProjectName(projectPath);
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		if(project == null) {
			project = new Project(projectName, projectPath);
			projectDao.persist(project);
		}
	}

	public static String extractProjectName(String path) {
		String fileSeparator = File.separator;
		String[] splitedPath = path.split("\\"+fileSeparator);
		String projectName = splitedPath[splitedPath.length - 2];
		return projectName;
	}

}
