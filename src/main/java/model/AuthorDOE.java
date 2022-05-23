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

}
