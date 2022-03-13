package analyzers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.alg.scoring.BetweennessCentrality;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import dao.FileVersionDAO;
import extractors.ProjectExtractor;
import model.Commit;
import model.File;
import model.FileVersion;
import model.Project;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class BetweennessCentralityAnalysis {
	
	public static void main(String[] args) {
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		run(files);
	}

	public static void run(List<File> files){
		FileVersionDAO fileVersionDAO = new FileVersionDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		Graph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		for (File file : files) {
			directedGraph.addVertex(file.getPath());
		}
		for (File file : files) {
			FileVersion fileVersion = fileVersionDAO.findByFileVersion(file, currentVersion);
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
		for (int i = 0; i < 10; i++) {
			System.out.println("BetweennessCentrality: "+files.get(i).getPath()+" Value: "+files.get(i).getScoreBetweennessCentrality());
		}
	}
}
