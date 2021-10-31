package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AuthorDOA {

	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private AuthorFile authorFile;
	@ManyToOne
	private Commit version;
	private Double degreeOfAuthorship;
	
	public AuthorDOA() {
	}
	public AuthorDOA(AuthorFile authorFile, Commit version, Double degreeOfAuthorship) {
		super();
		this.authorFile = authorFile;
		this.version = version;
		this.degreeOfAuthorship = degreeOfAuthorship;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public AuthorFile getAuthorFile() {
		return authorFile;
	}
	public void setAuthorFile(AuthorFile authorFile) {
		this.authorFile = authorFile;
	}
	public Commit getVersion() {
		return version;
	}
	public void setVersion(Commit version) {
		this.version = version;
	}
	public Double getDegreeOfAuthorship() {
		return degreeOfAuthorship;
	}
	public void setDegreeOfAuthorship(Double degreeOfAuthorship) {
		this.degreeOfAuthorship = degreeOfAuthorship;
	}
}
