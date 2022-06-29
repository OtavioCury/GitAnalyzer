package analyzers.truckfactor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckFactorDevelopersVO {

	private String name, email, project, dateVersion;

	public TruckFactorDevelopersVO(String name, String email, String project, String dateVersion) {
		super();
		this.name = name;
		this.email = email;
		this.project = project;
		this.dateVersion = dateVersion;
	}

	@Override
	public String toString() {
		return name+";"+email+";"+project+";"+dateVersion;
	}

}
