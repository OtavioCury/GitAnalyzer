package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import dao.FileDAO;
import dao.ProjectConstantsDAO;
import model.File;
import model.Project;
import model.ProjectConstants;

public class FileUtils {

	public static String returnFileExtension(String path) {
		String extension = path.substring(path.lastIndexOf("/")+1);
		extension = extension.substring(extension.indexOf(".")+1);
		return extension;
	}
	
	public static String returnFileName(String path) {
		String name = path.substring(path.lastIndexOf("/")+1);
		name = name.substring(0, name.indexOf("."));
		return name;
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
		filterFilesByExtensions(project, files);
		return files;
	}

	public static List<String> currentFiles() throws MissingObjectException, IncorrectObjectTypeException, IOException {
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
	
	public static HashMap<String, String> currentFilesWithContents() throws MissingObjectException, IncorrectObjectTypeException, IOException {
		Ref head = RepositoryAnalyzer.repository.exactRef("HEAD");
		HashMap<String, String> filesPath = new HashMap<String, String>();
		RevWalk walk = new RevWalk(RepositoryAnalyzer.repository);
		RevCommit commit = walk.parseCommit(head.getObjectId());
		RevTree tree = commit.getTree();
		TreeWalk treeWalk = new TreeWalk(RepositoryAnalyzer.repository);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(true);
		while (treeWalk.next()){
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = RepositoryAnalyzer.repository.open(objectId);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			loader.copyTo(stream);
			String content = stream.toString();
			filesPath.put(treeWalk.getPathString(), content);
		}
		treeWalk.close();
		walk.close();
		return filesPath;
	}
	
	public static LinkedHashMap<File, Double> filesCommitValues(Project project, List<File> files){
		FileDAO fileDAO = new FileDAO();
		LinkedHashMap<File, Double> filesValues = new LinkedHashMap<File, Double>();
		ProjectConstantsDAO projectConstantsDAO = new ProjectConstantsDAO();
		LinkedHashMap<File, Long> fileCommits = fileDAO.findOrderedMostCommited(project, files);
		int maior = 0;
		for(Map.Entry<File, Long> fileCommit: fileCommits.entrySet()) {
			if(fileCommit.getValue() > maior) {
				maior = fileCommit.getValue().intValue();
			}
		}
		for(Map.Entry<File, Long> fileCommit: fileCommits.entrySet()) {
			filesValues.put(fileCommit.getKey(), (double)fileCommit.getValue()/(double)maior);
		}
		return filesValues;
	}
	
	public static double sumValueFilesCommit(Project project, List<File> files) {
		double sum = 0;
		LinkedHashMap<File, Double> filesValues = filesCommitValues(project, files);
		for(Map.Entry<File, Double> fileCommit: filesValues.entrySet()) {
			sum = sum + filesValues.get(fileCommit.getKey());
		}
		return sum;
	}

	private static void filterFilesByExtensions(Project project, List<File> files) {
		List<File> removedFiles = new ArrayList<File>();
		ProjectConstantsDAO projectConstantsDAO = new ProjectConstantsDAO();
		ProjectConstants projectConstants = projectConstantsDAO.findByProject(project); 
		List<String> analyzedExtensions = projectConstants.getAnalyzedExtensions(); 
		for(File file: files) {
			if(analyzedExtensions.contains(file.getExtension()) == false) {
				removedFiles.add(file);
			}
		}
		files.removeAll(removedFiles);
	}
	
}
