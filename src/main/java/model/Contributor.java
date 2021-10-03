package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Contributor {
	
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String email;
	
	public Contributor(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	public Contributor() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
