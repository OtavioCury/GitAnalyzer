package analyzers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import extractors.ProjectExtractor;
import model.File;
import model.Project;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class SizeAnalysis {
	
	public static void main(String[] args) {
		ProjectExtractor projectExtractor = new ProjectExtractor();
		projectExtractor.run(args[0]);
		String projectName = projectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		LinkedHashMap<File, Double> filesSizeValues = FileUtils.filesSizeValues(project, files);
		int i = 1;
		for(Map.Entry<File, Double> fileSize: filesSizeValues.entrySet()) {
			if (i == 11) {
				break;
			}else {
				System.out.println("Size: "+fileSize.getKey().getPath()+" Value: "+fileSize.getValue());
			}
			i++;
		}
	}
}
