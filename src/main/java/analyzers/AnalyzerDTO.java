package analyzers;

import java.util.List;

import model.File;
import model.Project;

public class AnalyzerDTO {
	
	private Project project;
	private List<File> analyzedFiles;
	
	public AnalyzerDTO(Project project, List<File> analyzedFiles) {
		super();
		this.project = project;
		this.analyzedFiles = analyzedFiles;
	}
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public List<File> getAnalyzedFiles() {
		return analyzedFiles;
	}
	public void setAnalyzedFiles(List<File> analyzedFiles) {
		this.analyzedFiles = analyzedFiles;
	}
	
}
