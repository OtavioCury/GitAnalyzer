package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import dao.FileDAO;
import model.File;
import model.Project;

public class FileUtils {

	public static String returnFileExtension(String path) {
		String extension = path.substring(path.lastIndexOf("/")+1);
		extension = extension.substring(extension.indexOf(".")+1);
		return extension;
	}

	public static List<File> filesToBeAnalyzed(Project project){
		List<File> files = new ArrayList<File>();
		FileDAO fileDAO = new FileDAO();
		List<String> currentFilesPath = null;
		try {
			currentFilesPath = currentFiles();
			for(String filePath: currentFilesPath) {
				File file = fileDAO.findByPath(filePath, project);
				if(file != null) {
					files.add(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		filterFilesByExtensions(files);
		return files;
	}

	private static List<String> currentFiles() throws MissingObjectException, IncorrectObjectTypeException, IOException {
		Ref head = RepositoryAnalyzer.repository.exactRef("HEAD");
		List<String> filesPath = new ArrayList<String>();
		RevWalk walk = new RevWalk(RepositoryAnalyzer.repository);
		RevCommit commit = walk.parseCommit(head.getObjectId());
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(RepositoryAnalyzer.repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		while (treeWalk.next()) {
			filesPath.add(treeWalk.getPathString());
		}
		treeWalk.close();
		walk.close();
		return filesPath;
	}

	private static void filterFilesByExtensions(List<File> files) {
		List<File> removedFiles = new ArrayList<File>();
		for(File file: files) {
			if(Constants.analyzedExtensions.contains(file.getExtension()) == false) {
				removedFiles.add(file);
			}
		}
		files.removeAll(removedFiles);
	}

}
