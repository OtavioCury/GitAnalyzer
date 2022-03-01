package extractors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.CommitDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import dao.ContributorVersionDAO;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.ContributorVersion;
import model.File;
import utils.Constants;
import utils.RepositoryAnalyzer;

public class ContributorExtractor {
	
	private CommitDAO commitDAO = new CommitDAO();
	private CommitFileDAO commitFileDAO = new CommitFileDAO();

	public void run() {
		CommitDAO commitDAO = new CommitDAO();
		ContributorDAO authorDao = new ContributorDAO();
		ContributorVersionDAO contributorVersionDAO = new ContributorVersionDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		for(Contributor contributor: contributors) {
			if(contributorVersionDAO.existsContributorVersion(contributor, currentVersion) == false) {
				ContributorVersion contributorVersion = new ContributorVersion(contributor, currentVersion); 
				if(commitDAO.findLastCommitByContributor(contributor).before(Constants.thresholdDateDisable())
						|| isNotADev(contributor)) {
					contributorVersion.setDisabled(true);
				}
				contributorVersionDAO.persist(contributorVersion);
			}
		}
	}

	private boolean isNotADev(Contributor contributor) {
		List<Commit> commits = commitDAO.commitsByAuthor(contributor);
		int commitsNumber = commits.size(); 
		HashMap<Long, Integer> fileCommits = new HashMap<Long, Integer>();
		for(Commit commit: commits) {
			List<CommitFile> commitFile = commitFileDAO.findByCommit(commit);
			if(commitFile.size() == 1) {
				File file = commitFile.get(0).getFile();
				if(fileCommits.containsKey(file.getId())) {
					fileCommits.put(file.getId(), fileCommits.get(file.getId())+1);
				}else {
					fileCommits.put(file.getId(), 1);
				}
			}
		}
		int aux = 0;
		for(Map.Entry<Long, Integer> fileValue: fileCommits.entrySet()) {
			if(fileValue.getValue() > aux) {
				aux = fileValue.getValue();
			}
		}
		double percentage = (double)aux/(double)commitsNumber;
		if(percentage > 0.9) {
			return true;
		}
		return false;
	}

}
