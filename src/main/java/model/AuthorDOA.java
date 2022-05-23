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
public class AuthorDOA {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
