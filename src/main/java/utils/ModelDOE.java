package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dao.CommitFileDAO;
import model.CommitFile;
import model.Contributor;
import model.File;

public class ModelDOE {
	private static double intercept = 5.28223;
	private static double addsCoef = 0.23173;
	private static double faCoef = 0.36151;
	private static double numDaysCoef = -0.19421;
	private static double sizeCoef = -0.28761;

	public static double getDOE(int adds, int fa, int numDays, int size) {
		return intercept + (adds*addsCoef) + (fa*faCoef) + (numDays*numDaysCoef) + (size*sizeCoef);
	}
	
	public static double getContributorFileDOE(Contributor contributor, File file) {
		return getDOE(getAdds(contributor, file), getFA(contributor, file),
				getNumDays(contributor, file), file.getNumberLines());	
	}

	public static int getAdds(Contributor contributor, File file) {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		List<Contributor> contributors = FindAlias.getAlias(contributor);
		contributors.add(contributor);
		int adds = 0;
		List<CommitFile> commitsFile = new ArrayList<CommitFile>();
		for(Contributor contributorAux: contributors) {
			commitsFile.addAll(commitFileDao.findByAuthorFile(contributorAux, file));
		}
		for(CommitFile commitFile: commitsFile) {
			adds = adds + commitFile.getAdds();
		}
		return adds;
	}

	public static int getFA(Contributor contributor, File file) {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		List<Contributor> contributors = FindAlias.getAlias(contributor);
		contributors.add(contributor);
		int adds = 0;
		List<CommitFile> commitsFile = new ArrayList<CommitFile>();
		for(Contributor contributorAux: contributors) {
			if(commitFileDao.findByAuthorFileAdd(contributorAux, file)) {
				return 1;
			}
		}
		return 0;
	}

	public static int getNumDays(Contributor contributor, File file) {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		List<Contributor> contributors = FindAlias.getAlias(contributor);
		contributors.add(contributor);
		int adds = 0;
		List<Date> dates = new ArrayList<Date>();
		for(Contributor contributorAux: contributors) {
			dates.add(commitFileDao.findLastByAuthorFile(contributorAux, file));
		}
		Date maxDate = Collections.max(dates);
		Date currentDate = new Date();
		long diff = currentDate.getTime() - maxDate.getTime();
		int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return diffDays;
	}
}
