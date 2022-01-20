package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import analyzers.ProjectInitializer;
import dao.FileDAO;
import dao.ProjectConstantsDAO;
import dao.ProjectDAO;
import model.File;
import model.Project;
import model.ProjectConstants;

public class FileUtils {
	
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectInitializer.init(args[0]);
		String projectName = ProjectInitializer.extractProjectName(args[0]);
		System.out.println("================ Analyzing "+projectName+"============");
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		RepositoryAnalyzer.initRepository(projectName);
		double soma = 0;
		HashMap<File, Double> filesValues = filesValues(project);
		for(Map.Entry<File, Double> fileValue: filesValues.entrySet()) {
			soma = soma + fileValue.getValue();
		}
		System.out.println("Soma do valor de todos os arquivos: "+soma);
		System.out.println("================ End of Analysis ===========");
		RepositoryAnalyzer.git.close();
	}

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
		filterFilesByExtensions(project, files);
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
	
	public static LinkedHashMap<File, Double> filesValues(Project project){
		FileDAO fileDAO = new FileDAO();
		LinkedHashMap<File, Double> filesValues = new LinkedHashMap<File, Double>();
		LinkedHashMap<File, Long> fileCommits = fileDAO.findOrderedMostCommited(project, 
				project.getProjectConstants().getAnalyzedExtensions());
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
