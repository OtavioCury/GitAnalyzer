package analyzers;

import java.util.List;

import dao.CommitDAO;
import dao.ContributorDAO;
import dao.ContributorVersionDAO;
import model.Commit;
import model.Contributor;
import model.ContributorVersion;
import model.Project;
import utils.Constants;
import utils.RepositoryAnalyzer;

public class ContributorAnalyzer {
	
	private Project project;

	public ContributorAnalyzer(Project project) {
		this.project = project;
	}

	public void run() {
		CommitDAO commitDAO = new CommitDAO();
		ContributorDAO authorDao = new ContributorDAO();
		ContributorVersionDAO contributorVersionDAO = new ContributorVersionDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		for(Contributor contributor: contributors) {
			if(contributorVersionDAO.existsContributorVersion(contributor, currentVersion) == false) {
				ContributorVersion contributorVersion = new ContributorVersion(contributor, currentVersion); 
				if(commitDAO.findLastCommitByContributor(contributor).before(Constants.thresholdDateDisable())) {
					contributorVersion.setDisabled(true);
				}
				contributorVersionDAO.persist(contributorVersion);
			}
		}
	}

}
