package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

}
