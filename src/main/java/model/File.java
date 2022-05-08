package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class File {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String path;
	@ManyToOne
	private Project project;
	private String extension;

	@Transient
	private String content;
	@Transient
	private ReferenceSet referenceSet;
	@Transient
	private String packageName;
	@Transient 
	private int degreeReferences;
	@Transient
	private double scoreBetweennessCentrality;

	public File(String path, Project project, String extension) {
		super();
		this.path = path;
		this.project = project;
		this.extension = extension;
	}

	public File(String path) {
		super();
		this.path = path;
	}

	public File() {
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ReferenceSet getReferenceSet() {
		return referenceSet;
	}

	public void setReferenceSet(ReferenceSet referenceSet) {
		this.referenceSet = referenceSet;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getDegreeReferences() {
		return degreeReferences;
	}

	public void setDegreeReferences(int degreeReferences) {
		this.degreeReferences = degreeReferences;
	}

	public double getScoreBetweennessCentrality() {
		return scoreBetweennessCentrality;
	}

	public void setScoreBetweennessCentrality(double scoreBetweennessCentrality) {
		this.scoreBetweennessCentrality = scoreBetweennessCentrality;
	}

}
