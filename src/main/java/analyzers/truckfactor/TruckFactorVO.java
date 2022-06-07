package analyzers.truckfactor;

public class TruckFactorVO {

	private int numberAllDevs, numberAnalysedDevs, numberAnalysedDevsAlias, 
	numberAllFiles, numberAnalysedFiles, numberAllCommits, numberAnalysedCommits, truckfactor;
	private String projectName;

	public TruckFactorVO() {
		super();
	}

	public int getNumberAllDevs() {
		return numberAllDevs;
	}
	public void setNumberAllDevs(int numberAllDevs) {
		this.numberAllDevs = numberAllDevs;
	}
	public int getNumberAnalysedDevs() {
		return numberAnalysedDevs;
	}
	public void setNumberAnalysedDevs(int numberAnalysedDevs) {
		this.numberAnalysedDevs = numberAnalysedDevs;
	}
	public int getNumberAnalysedDevsAlias() {
		return numberAnalysedDevsAlias;
	}
	public void setNumberAnalysedDevsAlias(int numberAnalysedDevsAlias) {
		this.numberAnalysedDevsAlias = numberAnalysedDevsAlias;
	}
	public int getNumberAllFiles() {
		return numberAllFiles;
	}
	public void setNumberAllFiles(int numberAllFiles) {
		this.numberAllFiles = numberAllFiles;
	}
	public int getNumberAnalysedFiles() {
		return numberAnalysedFiles;
	}
	public void setNumberAnalysedFiles(int numberAnalysedFiles) {
		this.numberAnalysedFiles = numberAnalysedFiles;
	}
	public int getNumberAllCommits() {
		return numberAllCommits;
	}
	public void setNumberAllCommits(int numberAllCommits) {
		this.numberAllCommits = numberAllCommits;
	}
	public int getNumberAnalysedCommits() {
		return numberAnalysedCommits;
	}
	public void setNumberAnalysedCommits(int numberAnalysedCommits) {
		this.numberAnalysedCommits = numberAnalysedCommits;
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
		return numberAllDevs + ";" + numberAnalysedDevs
				+ ";" + numberAnalysedDevsAlias + ";" + numberAllFiles
				+ ";" + numberAnalysedFiles + ";" + numberAllCommits
				+ ";" + numberAnalysedCommits + ";" + truckfactor + ";"
				+ projectName;
	}

}
