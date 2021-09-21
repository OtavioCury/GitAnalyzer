package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Author {
	
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String email;
	
	public Author(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	public Author() {
	}
}
