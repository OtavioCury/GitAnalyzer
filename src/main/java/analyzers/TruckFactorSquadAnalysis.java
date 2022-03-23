package analyzers;

import java.util.List;
import java.util.Set;

import dao.ProjectVersionDAO;
import dao.SquadDAO;
import extractors.ProjectExtractor;
import model.Commit;
import model.File;
import model.Project;
import model.ProjectVersion;
import model.Squad;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class TruckFactorSquadAnalysis {
	
	public static void main(String[] args) {
		ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();
		SquadDAO squadDAO = new SquadDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		
		ProjectVersion projectVersion = projectVersionDAO.findByProjectVersion(project, currentVersion);
		if (projectVersion == null) {
			projectVersion = new ProjectVersion(project, currentVersion);
			projectVersionDAO.persist(projectVersion);
		}
		List<Squad> squads = squadDAO.listByProjectVersion(projectVersion);
		for (Squad squad : squads) {
			Set<File> files = FileUtils.squadFilesList(squad);
			System.out.println();
		}
	}

}
