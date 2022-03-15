package analyzers;

import java.util.List;

import extractors.ProjectExtractor;
import model.File;
import model.Project;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class Analyzer {
	
	protected static AnalyzerDTO getFiles(String projectPath) {
		ProjectExtractor.init(projectPath);
		String projectName = ProjectExtractor.extractProjectName(projectPath);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		AnalyzerDTO analyzerDTO = new AnalyzerDTO(project, files);
		return analyzerDTO;
	}
}
