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
		return Constants.intercept + (adds*Math.log(Constants.addsCoef + 1)) + (fa*Math.log(Constants.faCoef + 1)) 
				+ (numDays*Math.log(Constants.numDaysCoef + 1)) + (size*Math.log(Constants.sizeCoef + 1));
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
		for(Contributor contributorAux: contributors) {
			commitsFile.addAll(commitFileDao.findByAuthorFile(contributorAux, file));
		}
		for(CommitFile commitFile: commitsFile) {
			adds = adds + commitFile.getAdds();
		}
		return adds;
	}

	public int getFA(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		for(Contributor contributorAux: contributors) {
			if(commitFileDao.findByAuthorFileAdd(contributorAux, file)) {
				return 1;
			}
		}
		return 0;
	}

	public int getNumDays(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
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
