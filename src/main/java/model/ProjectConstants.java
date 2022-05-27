package model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	@ElementCollection
	private List<String> invalidPaths;

	public ProjectConstants(Project project, List<String> analyzedExtensions, 
			List<String> invalidCommits, List<String> invalidPaths) {
		super();
		this.project = project;
		this.analyzedExtensions = analyzedExtensions;
		this.invalidCommits = invalidCommits;
		this.invalidPaths = invalidPaths;
	}

	public ProjectConstants() {
		super();
	}
}