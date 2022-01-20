package model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ProjectConstants {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private Project project;
	@ElementCollection
	private List<String> analyzedExtensions;
	@ElementCollection
	private List<String> invalidCommits;
	
	public ProjectConstants(Project project, List<String> analyzedExtensions, List<String> invalidCommits) {
		super();
		this.project = project;
		this.analyzedExtensions = analyzedExtensions;
		this.invalidCommits = invalidCommits;
	}
	
	public ProjectConstants() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<String> getAnalyzedExtensions() {
		return analyzedExtensions;
	}

	public void setAnalyzedExtensions(List<String> analyzedExtensions) {
		this.analyzedExtensions = analyzedExtensions;
	}

	public List<String> getInvalidCommits() {
		return invalidCommits;
	}

	public void setInvalidCommits(List<String> invalidCommits) {
		this.invalidCommits = invalidCommits;
	}
}