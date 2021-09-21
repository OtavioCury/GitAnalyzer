package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
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

}
