package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getNumLines() {
		return numLines;
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;
	}

	public boolean isFirstAuthor() {
		return firstAuthor;
	}

	public void setFirstAuthor(boolean firstAuthor) {
		this.firstAuthor = firstAuthor;
	}
}
