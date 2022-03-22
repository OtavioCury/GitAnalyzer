package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.CommitFileDAO;
import dao.FileDAO;
import dao.FileRenameDAO;
import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.File;

public class MetricsUtils {

	protected static ContributorsUtils contributorsUtils = new ContributorsUtils();
	protected static CommitFileDAO commitFileDao = new CommitFileDAO();
	protected static FileDAO fileDAO = new FileDAO();
	protected static ProjectDAO projectDAO = new ProjectDAO();
	protected static FileRenameDAO fileRenameDAO = new FileRenameDAO();

	public static int getFA(Contributor contributor, File file) {
		List<Contributor> contributors = contributorsUtils.getAlias(contributor);
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		if(commitFileDao.findByAuthorsFileAdd(contributors, files)) {
			return 1;
		}
		return 0;
	}

	public static Set<File> getFilesRenames(File file){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Set<File> files = new HashSet<File>();
		files.add(file);
		boolean flag = false;
		while(flag == false) {
			List<File> newFiles = new ArrayList<File>();
			List<File> filesRename = fileRenameDAO.findByFile(files, currentCommit);
			for(File fileRename: filesRename) {
				boolean present = false;
				innerFor:for(File fileSet: files) {
					if(fileRename.getId().equals(fileSet.getId())) {
						present = true;
						break innerFor;
					}
				}
				if(present == false) {
					newFiles.add(fileRename);
				}
			}
			if(newFiles.size() == 0) {
				flag = true;
			}else {
				files.addAll(newFiles);
			}
		}
		return files;
	}

}
