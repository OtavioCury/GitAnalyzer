package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FileOtherPath {
	
	@Id
	@GeneratedValue
	private Long id;
	private String path;
	@ManyToOne
	private File file;
	@ManyToOne
	private Commit commitChange;
	
	public FileOtherPath() {
		super();
	}

	public FileOtherPath(String path, File file, Commit commitChange) {
		super();
		this.path = path;
		this.file = file;
		this.commitChange = commitChange;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Commit getCommitChange() {
		return commitChange;
	}

	public void setCommitChange(Commit commitChange) {
		this.commitChange = commitChange;
	}

}
