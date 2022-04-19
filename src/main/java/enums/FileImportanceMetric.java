package enums;

public enum FileImportanceMetric {
	
	SIZE("SIZE"), COMMITS("COMMITS"), 
	DEGREE_IN_OUT("DEGREE IN OUT"); 
	//BETWEENNESS_CENTRALITY("BETWEENNESS_CENTRALITY");
	
	private String name;

	private FileImportanceMetric(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
