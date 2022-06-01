package analyzers.truckfactor;

public class TruckFactorVO {

	private int numberOfDevs, numberOfDevsAlias, numberOfFiles, numberOfCommits, truckfactor;
	private String projectName;

	public TruckFactorVO(int numberOfDevs, int numberOfDevsAlias, int numberOfFiles, int numberOfCommits,
			int truckfactor, String projectName) {
		super();
		this.numberOfDevs = numberOfDevs;
		this.numberOfDevsAlias = numberOfDevsAlias;
		this.numberOfFiles = numberOfFiles;
		this.numberOfCommits = numberOfCommits;
		this.truckfactor = truckfactor;
		this.projectName = projectName;
	}
	public TruckFactorVO() {
		super();
	}
	public int getNumberOfDevs() {
		return numberOfDevs;
	}
	public void setNumberOfDevs(int numberOfDevs) {
		this.numberOfDevs = numberOfDevs;
	}
	public int getNumberOfDevsAlias() {
		return numberOfDevsAlias;
	}
	public void setNumberOfDevsAlias(int numberOfDevsAlias) {
		this.numberOfDevsAlias = numberOfDevsAlias;
	}
	public int getNumberOfFiles() {
		return numberOfFiles;
	}
	public void setNumberOfFiles(int numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}
	public int getNumberOfCommits() {
		return numberOfCommits;
	}
	public void setNumberOfCommits(int numberOfCommits) {
		this.numberOfCommits = numberOfCommits;
	}
	public int getTruckfactor() {
		return truckfactor;
	}
	public void setTruckfactor(int truckfactor) {
		this.truckfactor = truckfactor;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	@Override
	public String toString() {
		return "TruckFactorVO [numberOfDevs=" + numberOfDevs + ", numberOfDevsAlias=" + numberOfDevsAlias
				+ ", numberOfFiles=" + numberOfFiles + ", numberOfCommits=" + numberOfCommits + ", truckfactor="
				+ truckfactor + ", projectName=" + projectName + "]";
	}
}
