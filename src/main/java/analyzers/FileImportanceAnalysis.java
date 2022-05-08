package analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import enums.FileImportanceMetric;
import extractors.ProjectExtractor;
import model.File;
import model.Project;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class FileImportanceAnalysis {

	public static void main(String[] args) {
		HashMap<FileImportanceMetric, List<String>> metricList = new HashMap<FileImportanceMetric, List<String>>(); 
		
		ProjectExtractor projectExtractor = new ProjectExtractor();
		projectExtractor.run(args[0]);
		String projectName = projectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);

		FileImportanceMetric[] fileImportanceMetrics = FileImportanceMetric.values();
		for (FileImportanceMetric fileImportanceMetric : fileImportanceMetrics) {
			int i = 1;
			LinkedHashMap<File, Double> fileValue = FileUtils.filesValues(project, 
					files, fileImportanceMetric);
			List<String> filesMetric = new ArrayList<String>();
			for(Map.Entry<File, Double> fileEntry: fileValue.entrySet()) {
				if (i == 11) {
					break;
				}else {
					filesMetric.add(fileEntry.getKey().getPath());
				}
				i++;
			}
			metricList.put(fileImportanceMetric, filesMetric);
		}
		for(Map.Entry<FileImportanceMetric, List<String>> entry: metricList.entrySet()) {
			for(Map.Entry<FileImportanceMetric, List<String>> entry2: metricList.entrySet()) {
				if (entry.getKey().equals(entry2.getKey()) == false) {
					int hits = 0;
					for (String path : entry.getValue()) {
						for (String path2 : entry2.getValue()) {
							if (path.equals(path2)) {
								hits++;
								break;
							}
						}
					}
					System.out.println(entry.getKey().getName()+" - "+entry2.getKey().getName()+": "+hits);
				}
			}
		}
	}

}
