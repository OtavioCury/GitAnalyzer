package utils;

import java.util.ArrayList;
import java.util.List;

import dao.CommitFileDAO;
import dao.FileDAO;
import dao.FileRenameDAO;
import model.Commit;
import model.File;
import model.Project;

public class FileUtils {
	
	public static String returnFileExtension(String path) {
		String extension = path.substring(path.lastIndexOf("/")+1);
		extension = extension.substring(extension.indexOf(".")+1);
		return extension;
	}
	
	public static List<File> filesToBeAnalyzed(Project project){
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		CommitFileDAO commitFileDAO = new CommitFileDAO();
		FileDAO fileDAO = new FileDAO();
		FileRenameDAO fileRenameDAO = new FileRenameDAO();
		List<File> files = fileDAO.findByProjectExtensions(project, Constants.extensions);
		List<File> excludedFiles = new ArrayList<File>();
		for(File file: files) {
			if(fileRenameDAO.findByLastFileCommit(file, currentVersion) 
					|| commitFileDAO.findLastDelByFileVersion(file, currentVersion)) {
				excludedFiles.add(file);
			}
		}
		files.removeAll(excludedFiles);
		return files;
	}

}
