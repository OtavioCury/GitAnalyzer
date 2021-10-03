package main;

import java.util.List;

import dao.CommitFileDAO;
import model.CommitFile;

public class FixCommitFile {
	public static void main(String[] args) {
		CommitFileDAO commitFileDAO = new CommitFileDAO();
		List<CommitFile> commitsFiles = commitFileDAO.findAll();
		for(CommitFile commitFile: commitsFiles) {
			commitFile.setAdds(commitFile.getAdds()+commitFile.getMods());
			commitFile.setDels(commitFile.getDels()+commitFile.getMods());
			commitFileDAO.merge(commitFile);
		}
	}
}
