package utils;

import java.util.List;

import dao.CommitDAO;
import model.Commit;

public class CommitsUtils {
	
	public static Commit getCurrentVersion() {
		CommitDAO commitDAO = new CommitDAO();
		List<Commit> commits = commitDAO.commitsDescDate();
		if (commits.size() > 0) {
			return commits.get(0);
		}else {
			return null;
		}
	}
}
