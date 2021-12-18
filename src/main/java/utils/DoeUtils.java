package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dao.AuthorDoeDAO;
import dao.FileVersionDAO;
import dto.ContributorDTO;
import model.AuthorDOE;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;

public class DoeUtils extends MetricsUtils{

	private AuthorDoeDAO authorDoeDao = new AuthorDoeDAO();
	private FileVersionDAO fileCommitDAO = new FileVersionDAO();

	public DoeUtils(Commit currentCommit) {
		super();
		this.currentCommit = currentCommit;
	}

	public double getDOE(int adds, int fa, int numDays, int size) {
		double addsModel = Constants.addsCoefDoe*Math.log(adds + 1);
		double faModel = Constants.faCoefDoe*fa;
		double numDaysModel = Constants.numDaysCoefDoe*Math.log(numDays + 1);
		double sizeModel = Constants.sizeCoefDoe*Math.log(size);
		return Constants.interceptDoe + addsModel + faModel
				+ numDaysModel + sizeModel;
	}

	public double getContributorFileDOE(Contributor contributor, File file) {
		return getDOE(getAdds(contributor, file), getFA(contributor, file),
				getNumDays(contributor, file), getFileSize(file));	
	}

	private int getFileSize(File file) {
		return fileCommitDAO.numberLinesFileVersion(file, currentCommit);
	}

	private int getAdds(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		int adds = commitFileDao.sumAddsByAuthorsFileToVersion(contributors, file, currentCommit);
		return adds;
	}

	private int getNumDays(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		Date dateLastCommit = commitFileDao.findLastByAuthorsFileToVersion(contributors, file, currentCommit);
		Date currentDate = new Date();
		long diff = currentDate.getTime() - dateLastCommit.getTime();
		int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return diffDays;
	}

	public List<Contributor> getMantainersByFile(File file, double threshold){
		List<Contributor> mantainers = new ArrayList<Contributor>();
		List<AuthorDOE> does = authorDoeDao.findByFileVersion(file, currentCommit);
		AuthorDOE maxDoe = does.stream().max(Comparator.comparing(AuthorDOE::getDegreeOfExpertise)).get();
		for(AuthorDOE doe: does) {
			double normalizedDoe = doe.getDegreeOfExpertise()/maxDoe.getDegreeOfExpertise();
			if(normalizedDoe > threshold) {
				mantainers.add(doe.getAuthorFile().getAuthor());
			}
		}
		return mantainers;
	}

	public List<ContributorDTO> getMostKnowledgedByFile(String filePath, String projectName){
		Project project = projectDAO.findByName(projectName);
		File file = fileDAO.findByPath(filePath, project);
		List<ContributorDTO> contributors = new ArrayList<ContributorDTO>();
		Set<Contributor> contributorsAndAlias = new HashSet<Contributor>();
		while(contributors.size() < Constants.quantKnowledgedDevsByFile) {
			Contributor contributor = authorDoeDao.maxDoeByFileVersion(file, currentCommit, contributorsAndAlias);
			contributors.add(new ContributorDTO(contributor.getName(), contributor.getEmail()));
			contributorsAndAlias.add(contributor);
			contributorsAndAlias.addAll(findAlias.getAlias(contributor));
		}
		return contributors;
	}

}
