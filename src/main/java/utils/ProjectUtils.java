package utils;

import dao.ProjectDAO;
import model.Project;

public class ProjectUtils {
	
	public static Project getProjectByName(String projectName) {
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		return project;
	}

}
