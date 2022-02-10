package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class FileVersion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private File file;
	@ManyToOne
	private Commit version;
	private int numberLines;
	@OneToMany
	private List<File> filesReferencesGraph;
	
	public FileVersion(File file, Commit version, int numberLines) {
		super();
		this.file = file;
		this.version = version;
		this.numberLines = numberLines;
	}
	public FileVersion() {
		super();
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Commit getVersion() {
		return version;
	}
	public void setVersion(Commit version) {
		this.version = version;
	}
	public int getNumberLines() {
		return numberLines;
	}
	public void setNumberLines(int numberLines) {
		this.numberLines = numberLines;
	}
	public List<File> getFilesReferencesGraph() {
		return filesReferencesGraph;
	}
	public void setFilesReferencesGraph(List<File> filesReferencesGraph) {
		this.filesReferencesGraph = filesReferencesGraph;
	}

}
