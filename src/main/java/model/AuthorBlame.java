package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AuthorBlame {
	
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private AuthorFile authorFile;
	@ManyToOne
	private Commit version;
	private int numLines;
	
	public AuthorBlame(AuthorFile authorFile, Commit version, int numLines) {
		super();
		this.authorFile = authorFile;
		this.version = version;
		this.numLines = numLines;
	}
	
	public AuthorBlame() {
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
	public int getNumLines() {
		return numLines;
	}
	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}
}
