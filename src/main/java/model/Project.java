package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique=true)
	private String name;
	private String currentPath;
	@OneToOne
	private ProjectConstants projectConstants;
	
	public Project(String name, String currentPath) {
		super();
		this.name = name;
		this.currentPath = currentPath;
	}

	public Project() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public ProjectConstants getProjectConstants() {
		return projectConstants;
	}

	public void setProjectConstants(ProjectConstants projectConstants) {
		this.projectConstants = projectConstants;
	}

}
