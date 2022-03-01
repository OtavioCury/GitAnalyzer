package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class FileRename {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@OneToOne
	private File oldFile;
	@OneToOne
	private File newFile;
	@ManyToOne
	private Commit commitChange;
	
	public FileRename() {
		super();
	}
	public FileRename(File oldFile, File newFile, Commit commitChange) {
		super();
		this.oldFile = oldFile;
		this.newFile = newFile;
		this.commitChange = commitChange;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public File getOldFile() {
		return oldFile;
	}
	public void setOldFile(File oldFile) {
		this.oldFile = oldFile;
	}
	public File getNewFile() {
		return newFile;
	}
	public void setNewFile(File newFile) {
		this.newFile = newFile;
	}
	public Commit getCommitChange() {
		return commitChange;
	}
	public void setCommitChange(Commit commitChange) {
		this.commitChange = commitChange;
	}
	
}
