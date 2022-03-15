package enums;

public enum FileImportanceMetric {
	
	SIZE(1, "SIZE"), COMMITS(2, "COMMITS"), 
	DEGREE_IN_OUT(3, "DEGREE IN OUT"), BETWEENNESS_CENTRALITY(4, "BETWEENNESS_CENTRALITY");
	
	private int metric;
	private String name;

	private FileImportanceMetric(int metric, String name) {
		this.metric = metric;
		this.name = name;
	}

	public int getMetric() {
		return metric;
	}

	public void setMetric(int metric) {
		this.metric = metric;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
