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
	
	private CommitFileDAO commitFileDao = new CommitFileDAO();
	private FindAlias findAlias = new FindAlias();

	public double getDOE(int adds, int fa, int numDays, int size) {
		double addsModel = Constants.addsCoef*Math.log(adds + 1);
		double faModel = Constants.faCoef*Math.log(fa + 1);
		double numDaysModel = Constants.numDaysCoef*Math.log(numDays + 1);
		double sizeModel = Constants.sizeCoef*Math.log(size + 1);
		return Constants.intercept + addsModel + faModel
				+ numDaysModel + sizeModel;
	}
	
	public double getContributorFileDOE(Contributor contributor, File file) {
		return getDOE(getAdds(contributor, file), getFA(contributor, file),
				getNumDays(contributor, file), file.getNumberLines());	
	}

	public int getAdds(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		int adds = 0;
		List<CommitFile> commitsFile = new ArrayList<CommitFile>();
		commitsFile.addAll(commitFileDao.findByAuthorsFile(contributors, file));
		for(CommitFile commitFile: commitsFile) {
			adds = adds + commitFile.getAdds();
		}
		return adds;
	}

	public int getFA(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		if(commitFileDao.findByAuthorsFileAdd(contributors, file)) {
			return 1;
		}
		return 0;
	}

	public int getNumDays(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		List<Date> dates = new ArrayList<Date>();
		dates.add(commitFileDao.findLastByAuthorsFile(contributors, file));
		Date maxDate = Collections.max(dates);
		Date currentDate = new Date();
		long diff = currentDate.getTime() - maxDate.getTime();
		int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return diffDays;
	}
}
