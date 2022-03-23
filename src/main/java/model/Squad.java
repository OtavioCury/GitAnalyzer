package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Squad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@ManyToMany
	private List<Contributor> members;
	@ManyToOne
	private ProjectVersion projectVersion;
	
	@Transient
	private List<File> files;
	
	public Squad() {
	}
	
	public Squad(String name, List<Contributor> members, ProjectVersion projectVersion) {
		super();
		this.name = name;
		this.members = members;
		this.projectVersion = projectVersion;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Contributor> getMembers() {
		return members;
	}
	public void setMembers(List<Contributor> members) {
		this.members = members;
	}
	public ProjectVersion getProjectVersion() {
		return projectVersion;
	}
	public void setProjectVersion(ProjectVersion projectVersion) {
		this.projectVersion = projectVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
	
}
