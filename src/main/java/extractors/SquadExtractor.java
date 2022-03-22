package extractors;

import java.util.ArrayList;
import java.util.List;

import dao.ContributorDAO;
import dao.ProjectVersionDAO;
import dao.SquadDAO;
import model.Commit;
import model.Contributor;
import model.Project;
import model.ProjectVersion;
import model.Squad;
import utils.RepositoryAnalyzer;

public class SquadExtractor {
	
	private Project project;
	private ContributorDAO contributorDAO = new ContributorDAO();
	private ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();
	private SquadDAO squadDAO = new SquadDAO();

	public SquadExtractor(Project project) {
		super();
		this.project = project;
	}
	
	public void run() {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		if (project != null) {
			ProjectVersion projectVersion = projectVersionDAO.findByProjectVersion(project, currentCommit);
			if (projectVersion == null) {
				projectVersion = new ProjectVersion(project, currentCommit);
				projectVersionDAO.persist(projectVersion);
			}
			if(project.getName().trim().toUpperCase().equals("IHEALTH")) {
				if (squadDAO.existsByNameProjectVersion("PATAMON", projectVersion) == false) {
					Squad squad = new Squad("PATAMON", devsPatamonSquad(), projectVersion);
					squadDAO.persist(squad);
				}
			}
		}
	}

	private List<Contributor> devsPatamonSquad() {
		List<Contributor> contributors = new ArrayList<Contributor>();
		contributors.add(contributorDAO.findByEmailProject("otaviocury.oc@gmail.com", project));
		contributors.add(contributorDAO.findByEmailProject("fchagas.sousa45@gmail.com", project));
		contributors.add(contributorDAO.findByEmailProject("ro.goncalve@gmail.com", project));
		return contributors;
	}

}
