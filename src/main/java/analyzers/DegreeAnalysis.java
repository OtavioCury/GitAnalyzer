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

public class DegreeAnalysis {
	
	public static void main(String[] args) {
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		LinkedHashMap<File, Double> filesDegreeValues = FileUtils.filesDegreeValues(project, files);
		int i = 1;
		for(Map.Entry<File, Double> fileCommit: filesDegreeValues.entrySet()) {
			if (i == 11) {
				break;
			}else {
				System.out.println("File degree: "+fileCommit.getKey().getPath()+" Value: "+fileCommit.getValue());
			}
			i++;
		}
	}
	
}
