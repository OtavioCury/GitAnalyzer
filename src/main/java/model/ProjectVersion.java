package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProjectVersion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private Project project;
	@ManyToOne
	private Commit version;
	
	public ProjectVersion(Project project, Commit version) {
		super();
		this.project = project;
		this.version = version;
	}
	public ProjectVersion() {
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
	public Commit getVersion() {
		return version;
	}
	public void setVersion(Commit version) {
		this.version = version;
	}
}
