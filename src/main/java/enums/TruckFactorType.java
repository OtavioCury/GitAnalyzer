package enums;

public enum TruckFactorType {
	
	CLASSICAL("CLASSICAL"), FILE_IMPORTANCE_AWARE("FILE_IMPORTANCE_AWARE"); 
	
	private String name;
	private TruckFactorType(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
