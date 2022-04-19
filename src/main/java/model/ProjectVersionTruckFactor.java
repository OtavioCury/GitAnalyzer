package model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import enums.FileImportanceMetric;
import enums.KnowledgeMetric;
import enums.TruckFactorType;

@Entity
public class ProjectVersionTruckFactor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	private ProjectVersion projectVersion;
	@ManyToMany @JoinTable(name = "projectversiontruckfactor_topcontributors")
	private List<Contributor> topContributors;
	@ManyToMany @JoinTable(name = "projectversiontruckfactor_abandonedfiles")
	private List<File> abandonedFiles;
	private int truckFactor;
	@Enumerated(EnumType.STRING)
	private KnowledgeMetric knowledgeMetric;
	@Enumerated(EnumType.STRING)
	private FileImportanceMetric fileImportanceMetric;
	@Enumerated(EnumType.STRING)
	private TruckFactorType truckFactorType;

	public ProjectVersionTruckFactor(ProjectVersion projectVersion, List<Contributor> topContributors,
			KnowledgeMetric knowledgeMetric, FileImportanceMetric fileImportanceMetric,
			TruckFactorType truckFactorType, List<File> abandonedFiles) {
		super();
		this.abandonedFiles = abandonedFiles;
		this.projectVersion = projectVersion;
		this.topContributors = topContributors;
		truckFactor = topContributors.size();
		this.knowledgeMetric = knowledgeMetric;
		this.fileImportanceMetric = fileImportanceMetric;
		this.truckFactorType = truckFactorType;
	}
	public ProjectVersionTruckFactor() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProjectVersion getProjectVersion() {
		return projectVersion;
	}
	public void setProjectVersion(ProjectVersion projectVersion) {
		this.projectVersion = projectVersion;
	}
	public KnowledgeMetric getKnowledgeMetric() {
		return knowledgeMetric;
	}
	public void setKnowledgeMetric(KnowledgeMetric knowledgeMetric) {
		this.knowledgeMetric = knowledgeMetric;
	}
	public FileImportanceMetric getFileImportanceMetric() {
		return fileImportanceMetric;
	}
	public void setFileImportanceMetric(FileImportanceMetric fileImportanceMetric) {
		this.fileImportanceMetric = fileImportanceMetric;
	}
	public TruckFactorType getTruckFactorType() {
		return truckFactorType;
	}
	public void setTruckFactorType(TruckFactorType truckFactorType) {
		this.truckFactorType = truckFactorType;
	}
	public List<Contributor> getTopContributors() {
		return topContributors;
	}
	public void setTopContributors(List<Contributor> topContributors) {
		this.topContributors = topContributors;
	}
	public int getTruckFactor() {
		return truckFactor;
	}
	public void setTruckFactor(int truckFactor) {
		this.truckFactor = truckFactor;
	}
	public List<File> getAbandonedFiles() {
		return abandonedFiles;
	}
	public void setAbandonedFiles(List<File> abandonedFiles) {
		this.abandonedFiles = abandonedFiles;
	}
}
