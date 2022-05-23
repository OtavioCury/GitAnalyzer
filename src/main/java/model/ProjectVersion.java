package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
}
