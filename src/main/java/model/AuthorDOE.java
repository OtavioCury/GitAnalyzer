package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AuthorDOE {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private AuthorFile authorFile;
	@ManyToOne
	private Commit version;
	private Double degreeOfExpertise;
	
	public AuthorDOE() {
	}
	public AuthorDOE(AuthorFile authorFile, Commit version, Double degreeOfExpertise) {
		this.authorFile = authorFile;
		this.version = version;
		this.degreeOfExpertise = degreeOfExpertise;
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
	public Double getDegreeOfExpertise() {
		return degreeOfExpertise;
	}
	public void setDegreeOfExpertise(Double degreeOfExpertise) {
		this.degreeOfExpertise = degreeOfExpertise;
	}

}
