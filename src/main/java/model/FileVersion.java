package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FileVersion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private File file;
	@ManyToOne
	private Commit version;
	private int numberLines;
	@ManyToMany @JoinTable(name="file_references")
	private List<File> filesReferencesGraphOut;

	public FileVersion(File file, Commit version, int numberLines) {
		super();
		this.file = file;
		this.version = version;
		this.numberLines = numberLines;
	}
	public FileVersion() {
		super();
	}

}
