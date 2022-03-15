package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import dao.FileDAO;
import dao.FileVersionDAO;
import dao.ProjectConstantsDAO;
import enums.FileImportanceMetric;
import model.Commit;
import model.File;
import model.FileVersion;
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
	
	public static LinkedHashMap<File, Double> filesValues(Project project, List<File> files, 
			FileImportanceMetric fileImportanceMetric){
		if (fileImportanceMetric.equals(FileImportanceMetric.SIZE)) {
			return filesSizeValues(project, files);
		}else if(fileImportanceMetric.equals(FileImportanceMetric.COMMITS)) {
			return filesCommitValues(project, files);
		}else if(fileImportanceMetric.equals(FileImportanceMetric.BETWEENNESS_CENTRALITY)) {
			return filesBetweennessCentralityValues(project, files);
		}else if(fileImportanceMetric.equals(FileImportanceMetric.DEGREE_IN_OUT)) {
			return filesDegreeValues(project, files);
		}
		return null;
	}

	public static LinkedHashMap<File, Double> filesCommitValues(Project project, List<File> files){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		FileDAO fileDAO = new FileDAO();
		LinkedHashMap<File, Double> filesValues = new LinkedHashMap<File, Double>();
		LinkedHashMap<File, Long> fileCommits = fileDAO.findOrderedMostCommited(project, files, currentCommit);
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
	
	public static LinkedHashMap<File, Double> filesSizeValues(Project project, List<File> files) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		FileVersionDAO filesVersionDAO = new FileVersionDAO();
		List<FileVersion> filesVersions = new ArrayList<FileVersion>();
		for (File file : files) {
			filesVersions.add(filesVersionDAO.findByFileVersion(file, currentCommit));
		}
		Collections.sort(filesVersions, new Comparator<FileVersion>() {
		    @Override
		    public int compare(FileVersion f1, FileVersion f2) {
		        return Integer.compare(f2.getNumberLines(), f1.getNumberLines());
		    }
		});
		LinkedHashMap<File, Double> filesSizeValues = new LinkedHashMap<File, Double>();
		for (FileVersion fileVersion : filesVersions) {
			filesSizeValues.put(fileVersion.getFile(), (double)fileVersion.getNumberLines()/(double)filesVersions.get(0).getNumberLines());
		}
		return filesSizeValues;
	}

	public static LinkedHashMap<File, Double> filesDegreeValues(Project project, List<File> files){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		FileVersionDAO filesVersionDAO = new FileVersionDAO();
		List<FileVersion> filesVersions = new ArrayList<FileVersion>();
		for (File file: files) {
			FileVersion fileVersion = filesVersionDAO.findByFileVersion(file, currentCommit);
			if (fileVersion != null) {
				filesVersions.add(fileVersion);
			}
		}
		for (File file : files) {
			for (FileVersion fileVersion : filesVersions) {
				if (file.getId().equals(fileVersion.getFile().getId())) {
					file.setDegreeReferences(fileVersion.getFilesReferencesGraphOut().size());
					file.setDegreeReferences(file.getDegreeReferences()+countReferencesToFile(file, filesVersions));
				}
			}
		}
		Collections.sort(files, Comparator.comparingInt(File::getDegreeReferences).reversed());
		LinkedHashMap<File, Double> filesDegreeValues = new LinkedHashMap<File, Double>();
		for (File fileAux: files) {
			filesDegreeValues.put(fileAux, (double)fileAux.getDegreeReferences()/(double)files.get(0).getDegreeReferences());
		}
		return filesDegreeValues;
	}
	
	public static LinkedHashMap<File, Double> filesBetweennessCentralityValues(Project project, List<File> files){
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		FileVersionDAO filesVersionDAO = new FileVersionDAO();
		Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		for (File file : files) {
			directedGraph.addVertex(file.getPath());
		}
		for (File file : files) {
			FileVersion fileVersion = filesVersionDAO.findByFileVersion(file, currentVersion);
			List<File> filesOut = fileVersion.getFilesReferencesGraphOut();
			for (File fileOut : filesOut) {
				directedGraph.addEdge(file.getPath(), fileOut.getPath());
			}
		}
		BetweennessCentrality<String, DefaultEdge> bt = new BetweennessCentrality<String, DefaultEdge>(directedGraph);
		Map<String, Double> map = bt.getScores();
		for(Map.Entry<String, Double> mapFile: map.entrySet()) {
			for (File file : files) {
				if (file.getPath().equals(mapFile.getKey())) {
					file.setScoreBetweennessCentrality(mapFile.getValue());
				}
			}
		}
		Collections.sort(files, new Comparator<File>() {
		    @Override
		    public int compare(File f1, File f2) {
		        return Double.compare(f2.getScoreBetweennessCentrality(), f1.getScoreBetweennessCentrality());
		    }
		});
		LinkedHashMap<File, Double> filesBetweennessValues = new LinkedHashMap<File, Double>();
		for (File fileAux: files) {
			filesBetweennessValues.put(fileAux, 
					(double)fileAux.getScoreBetweennessCentrality()/(double)files.get(0).getScoreBetweennessCentrality());
		}
		return filesBetweennessValues;
	}

	public static int countReferencesToFile(File file, List<FileVersion> filesVersions) {
		int references = 0;
		for (FileVersion fileVersion: filesVersions) {
			if (fileVersion.getFile().getId().equals(file.getId()) == false) {
				for (File fileReference : fileVersion.getFilesReferencesGraphOut()) {
					if (fileReference.getId().equals(file.getId())) {
						references++;
					}
				}
			}
		}
		return references;
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