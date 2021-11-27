package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class File {
	
	@Id
	@GeneratedValue
	private Long id;
	private String path;
	@ManyToOne
	private Project project;
	private String extension;
	@OneToOne
	private File originalFile;
	
	public File(String path, Project project, String extension) {
		super();
		this.path = path;
		this.project = project;
		this.extension = extension;
	}
	
	public File(String path, Project project, String extension, File originalFile) {
		super();
		this.path = path;
		this.project = project;
		this.extension = extension;
		this.originalFile = originalFile;
	}

	public File() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public File getOriginalFile() {
		return originalFile;
	}

	public void setOriginalFile(File originalFile) {
		this.originalFile = originalFile;
	}
}
