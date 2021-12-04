package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class FileCommit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private File file;
	@ManyToOne
	private Commit commit;
	private int numberLines;
	
	public FileCommit(File file, Commit commit, int numberLines) {
		super();
		this.file = file;
		this.commit = commit;
		this.numberLines = numberLines;
	}
	public FileCommit() {
		super();
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Commit getCommit() {
		return commit;
	}
	public void setCommit(Commit commit) {
		this.commit = commit;
	}
	public int getNumberLines() {
		return numberLines;
	}
	public void setNumberLines(int numberLines) {
		this.numberLines = numberLines;
	}

}
