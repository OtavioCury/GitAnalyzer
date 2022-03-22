package enums;

public enum KnowledgeMetric {
	DOA("DOA"), DOE("DOE");
	
	private String name;

	private KnowledgeMetric(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
