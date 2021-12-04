package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class AuthorFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private Contributor author;
	@OneToOne
	private File file;
	private boolean firstAuthor;
	
	public AuthorFile(Contributor author, File file, boolean firstAuthor) {
		super();
		this.author = author;
		this.file = file;
		this.setFirstAuthor(firstAuthor);
	}

	public AuthorFile() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Contributor getAuthor() {
		return author;
	}

	public void setAuthor(Contributor author) {
		this.author = author;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isFirstAuthor() {
		return firstAuthor;
	}

	public void setFirstAuthor(boolean firstAuthor) {
		this.firstAuthor = firstAuthor;
	}
}
