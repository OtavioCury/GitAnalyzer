package extractors;

import java.io.File;
import java.util.Arrays;

import dao.ProjectConstantsDAO;
import dao.ProjectDAO;
import model.Project;
import model.ProjectConstants;

public class ProjectExtractor {

	public static void init(String projectPath) {
		String projectName = extractProjectName(projectPath);
		ProjectDAO projectDao = new ProjectDAO();
		ProjectConstantsDAO projectConstantsDao = new ProjectConstantsDAO();
		Project project = projectDao.findByName(projectName);
		if(project == null) {
			project = new Project(projectName, projectPath);
			projectDao.persist(project);
			ProjectConstants projectConstants = null;
			if(projectName.trim().toUpperCase().equals("IHEALTH")) {
				projectConstants = new ProjectConstants(project, 
						Arrays.asList(new String[]{"jhm.xml", "java", "jsp", "tag"}), 
						Arrays.asList(new String[]{"ad7a3b429dbf7cbcc79ad6efe8789bfd8dbb216e"}),
						Arrays.asList(new String[]{"target"}));
				projectConstantsDao.persist(projectConstants);
			}
			project.setProjectConstants(projectConstants);
			projectDao.merge(project);
		}
	}

	public static String extractProjectName(String path) {
		String fileSeparator = File.separator;
		String[] splitedPath = path.split("\\"+fileSeparator);
		String projectName = splitedPath[splitedPath.length - 1];
		return projectName;
	}

}
