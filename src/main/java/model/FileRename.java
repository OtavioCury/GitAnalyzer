package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
	
}
