package model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
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

	public Contributor() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getNumberFilesAuthor() {
		return numberFilesAuthor;
	}

	public void setNumberFilesAuthor(int numberFilesAuthor) {
		this.numberFilesAuthor = numberFilesAuthor;
	}
	
	public double getSumFileImportance() {
		return sumFileImportance;
	}

	public void setSumFileImportance(double sumFileImportance) {
		this.sumFileImportance = sumFileImportance;
	}

	public boolean isNotDev() {
		return isNotDev;
	}

	public void setNotDev(boolean isNotDev) {
		this.isNotDev = isNotDev;
	}

	public Set<Contributor> getAlias() {
		return alias;
	}

	public void setAlias(Set<Contributor> alias) {
		this.alias = alias;
	}
}
