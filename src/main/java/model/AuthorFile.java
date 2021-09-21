package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class AuthorFile {
	
	@Id
	@GeneratedValue
	private Long id;
	@OneToOne
	private Author author;
	@OneToOne
	private File file;
	private int numLines;
	private boolean firstAuthor;
	
	public AuthorFile(Author author, File file, int numLines) {
		super();
		this.author = author;
		this.file = file;
		this.numLines = numLines;
	}
	
	public AuthorFile() {
	}
}
