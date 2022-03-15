package enums;

public enum KnowledgeMetric {
	DOA(1, "DOA"), DOE(2, "DOE");
	
	private int metric;
	private String name;

	private KnowledgeMetric(int metric, String name) {
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
