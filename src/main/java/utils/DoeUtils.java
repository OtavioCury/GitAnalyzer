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

	private static AuthorDoeDAO authorDoeDao = new AuthorDoeDAO();
	private static FileVersionDAO fileCommitDAO = new FileVersionDAO();

	public static double getDOE(int adds, int fa, int numDays, int size) {
		double addsModel = Constants.addsCoefDoe*Math.log(adds + 1);
		double faModel = Constants.faCoefDoe*fa;
		double numDaysModel = Constants.numDaysCoefDoe*Math.log(numDays + 1);
		double sizeModel = Constants.sizeCoefDoe*Math.log(size);
		return Constants.interceptDoe + addsModel + faModel
				+ numDaysModel + sizeModel;
	}

	public static double getContributorFileDOE(Contributor contributor, File file) {
		return getDOE(getAdds(contributor, file), getFA(contributor, file),
				getNumDays(contributor, file), getFileSize(file));	
	}

	private static int getFileSize(File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		return fileCommitDAO.numberLinesFileVersion(file, currentCommit);
	}

	private static int getAdds(Contributor contributor, File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> contributors = contributorsUtils.getAlias(contributor);
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		int adds = commitFileDao.sumAddsByAuthorsFileToVersion(contributors, files, currentCommit);
		return adds;
	}

	private static int getNumDays(Contributor contributor, File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> contributors = contributorsUtils.getAlias(contributor);
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		Date dateLastCommit = commitFileDao.findLastByAuthorsFileToVersion(contributors, files, currentCommit);
		Date currentDate = new Date();
		long diff = currentDate.getTime() - dateLastCommit.getTime();
		int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return diffDays;
	}

	public static List<Contributor> getMantainersByFile(File file, double threshold){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> mantainers = new ArrayList<Contributor>();
		List<AuthorDOE> does = authorDoeDao.findByFileVersion(file, currentCommit);
		if(does != null && does.size() > 0) {
			AuthorDOE maxDoe = does.stream().max(Comparator.comparing(AuthorDOE::getDegreeOfExpertise)).get();
			for(AuthorDOE doe: does) {
				double normalizedDoe = doe.getDegreeOfExpertise()/maxDoe.getDegreeOfExpertise();
				if(normalizedDoe > threshold) {
					mantainers.add(doe.getAuthorFile().getAuthor());
				}
			}
		}
		return mantainers;
	}

	public List<ContributorDTO> getMostKnowledgedByFile(String filePath, String projectName){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Project project = projectDAO.findByName(projectName);
		File file = fileDAO.findByPath(filePath, project);
		Set<File> files = getFilesRenames(file);
		List<ContributorDTO> contributors = new ArrayList<ContributorDTO>();
		Set<Contributor> contributorsAndAlias = new HashSet<Contributor>();
		while(contributors.size() < Constants.quantKnowledgedDevsByFile) {
			Contributor contributor = authorDoeDao.maxDoeByFileVersion(files, currentCommit, contributorsAndAlias);
			contributors.add(new ContributorDTO(contributor.getName(), contributor.getEmail()));
			contributorsAndAlias.add(contributor);
			contributorsAndAlias.addAll(contributorsUtils.getAlias(contributor));
		}
		return contributors;
	}

}
