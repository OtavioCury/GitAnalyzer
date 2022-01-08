package analyzers;

import java.util.List;

import dao.CommitDAO;
import dao.ContributorDAO;
import dao.ContributorVersionDAO;
import model.Commit;
import model.Contributor;
import model.ContributorVersion;
import utils.Constants;
import utils.RepositoryAnalyzer;

public class ContributorAnalyzer {

	ContributorVersionDAO contributorVersionDAO = new ContributorVersionDAO();

	public void run() {
		CommitDAO commitDAO = new CommitDAO();
		ContributorDAO authorDao = new ContributorDAO();
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
