package utils;

import java.util.List;

import dao.CommitFileDAO;
import dao.FileDAO;
import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.File;

public class MetricsUtils {
	
	protected FindAlias findAlias = new FindAlias();
	protected Commit currentCommit;
	protected CommitFileDAO commitFileDao = new CommitFileDAO();
	protected FileDAO fileDAO = new FileDAO();
	protected ProjectDAO projectDAO = new ProjectDAO();

	public int getFA(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		if(commitFileDao.findByAuthorsFileAdd(contributors, file)) {
			return 1;
		}
		return 0;
	}

}
