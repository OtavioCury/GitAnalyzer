package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import dao.AuthorDoeDAO;
import dao.FileVersionDAO;
import model.AuthorDOE;
import model.Commit;
import model.Contributor;
import model.File;

public class DoeUtils extends MetricsUtils{

	private AuthorDoeDAO authorDoeDao = new AuthorDoeDAO();
	private FileVersionDAO fileCommitDAO = new FileVersionDAO();

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
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		return fileCommitDAO.numberLinesFileVersion(file, currentCommit);
	}

	private int getAdds(Contributor contributor, File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Set<File> files = getFilesRenames(file);
		int adds = commitDAO.sumAddsByAuthorsFileToVersion(contributorAndAliasIds(contributor), fileAndRenamesIds(files), currentCommit);
		return adds;
	}

	private int getNumDays(Contributor contributor, File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Set<File> files = getFilesRenames(file);
		Date dateLastCommit = commitDAO.findLastByAuthorsFileToVersion(contributorAndAliasIds(contributor), fileAndRenamesIds(files), currentCommit);
		Date currentDate = new Date();
		long diff = currentDate.getTime() - dateLastCommit.getTime();
		int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return diffDays;
	}

	public List<Contributor> getMantainersByFile(File file){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> mantainers = new ArrayList<Contributor>();
		List<AuthorDOE> does = authorDoeDao.findByFileVersion(file, currentCommit);
		if(does != null && does.size() > 0) {
			AuthorDOE maxDoe = does.stream().max(Comparator.comparing(AuthorDOE::getDegreeOfExpertise)).get();
			for(AuthorDOE doe: does) {
				double normalizedDoe = doe.getDegreeOfExpertise()/maxDoe.getDegreeOfExpertise();
				if(normalizedDoe > Constants.normalizedThresholdMantainerDOE) {
					mantainers.add(doe.getAuthorFile().getAuthor());
				}
			}
		}
		return mantainers;
	}

}
