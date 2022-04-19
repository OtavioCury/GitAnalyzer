package extractors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.CommitDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.File;
import model.Project;

public class ContributorExtractor {

	private CommitDAO commitDAO = new CommitDAO();
	private CommitFileDAO commitFileDAO = new CommitFileDAO();
	private ContributorDAO contributorDao = new ContributorDAO();
	private Project project;

	public ContributorExtractor(Project project) {
		super();
		this.project = project;
	}

	public void run() {
		List<Contributor> contributors = contributorDao.findAll(Contributor.class);
		for(Contributor contributor: contributors) {
			if(isNotADev(contributor)) {
				contributor.setNotDev(true);
			}
			contributorDao.merge(contributor);
		}
	}

	private boolean isNotADev(Contributor contributor) {
		if (contributor.getEmail().equals("f.bruno.rocha@gmail.com")) {
			return true;
		}
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
