package model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Contributor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	@ManyToOne
	private Project project;
	private boolean isNotDev;
	
	@Transient
	private int numberFilesAuthor;
	@Transient
	private double sumFileImportance;
	@Transient
	private Set<Contributor> alias;
	
	public Contributor(String name, String email, Project project) {
		super();
		this.name = name;
		this.email = email;
		this.project = project;
	}
	
	public Contributor(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}

	public Contributor() {
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(email, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contributor other = (Contributor) obj;
		return Objects.equals(email, other.email) && Objects.equals(name, other.name);
	}
}
