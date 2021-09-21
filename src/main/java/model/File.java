package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class File {
	
	@Id
	@GeneratedValue
	private Long id;
	private String path;
	@ManyToOne
	private Project project;
	private int numberLines;
	private String extension;
	
	public File(String path, Project project, String extension) {
		super();
		this.path = path;
		this.project = project;
		this.extension = extension;
	}
	
	public File() {
	}
	
}
