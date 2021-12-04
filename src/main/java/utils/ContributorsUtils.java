package utils;

import java.util.ArrayList;
import java.util.List;

import dao.ContributorDAO;
import dao.ContributorVersionDAO;
import model.Commit;
import model.Contributor;
import model.Project;

public class ContributorsUtils {
	
	public static List<Contributor> activeContributors(Project project){
		ContributorDAO contributorDAO = new ContributorDAO();
		ContributorVersionDAO contributorVesionDAO = new ContributorVersionDAO();
		List<Contributor> contributors = contributorDAO.findByProject(project);
		List<Contributor> contributorsExcluded = new ArrayList<Contributor>();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		for(Contributor contributor: contributors) {
			if(contributorVesionDAO.disabledContributorVersion(contributor, currentVersion)) {
				contributorsExcluded.add(contributor);
			}
		}
		contributors.removeAll(contributorsExcluded);
		return contributors;
	}
}
