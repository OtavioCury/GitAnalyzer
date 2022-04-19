package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.CommitDAO;
import dao.FileDAO;
import dao.FileRenameDAO;
import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.File;

public class MetricsUtils {

	protected FileDAO fileDAO = new FileDAO();
	protected ProjectDAO projectDAO = new ProjectDAO();
	protected FileRenameDAO fileRenameDAO = new FileRenameDAO();
	protected CommitDAO commitDAO = new CommitDAO();

	public int getFA(Contributor contributor, File file) {
		Set<File> files = getFilesRenames(file);
		if(commitDAO.findByAuthorsFileAdd(contributorAndAliasIds(contributor), fileAndRenamesIds(files))) {
			return 1;
		}
		return 0;
	}

	public Set<File> getFilesRenames(File file){
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

	protected Set<Long> contributorAndAliasIds(Contributor contributor){
		Set<Long> ids = new HashSet<Long>();
		ids.add(contributor.getId());
		for (Contributor alias: contributor.getAlias()) {
			ids.add(alias.getId());
		}
		return ids;
	}

	protected Set<Long> fileAndRenamesIds(Set<File> files){
		Set<Long> ids = new HashSet<Long>();
		for (File file: files) {
			ids.add(file.getId());
		}
		return ids;
	}
}
