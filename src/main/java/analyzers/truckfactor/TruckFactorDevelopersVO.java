package analyzers.truckfactor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckFactorDevelopersVO {

	private String name, email, project;

	public TruckFactorDevelopersVO(String name, String email, String project) {
		super();
		this.name = name;
		this.email = email;
		this.project = project;
	}

	@Override
	public String toString() {
		return name+";"+email+";"+project;
	}

}
