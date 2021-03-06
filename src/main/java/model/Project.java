package model;

import javax.persistence.Column;
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
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique=true)
	private String name;
	private String currentPath;
	@OneToOne
	private ProjectConstants projectConstants;
	private boolean commitsExtracted;

	public Project(String name, String currentPath) {
		super();
		this.name = name;
		this.currentPath = currentPath;
	}

	public Project(String name) {
		this.name = name;
	}

	public Project() {
		super();
	}

}
