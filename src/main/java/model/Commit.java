package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Commit {
	
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private Author author;
	private Date date;
	private String externalId;
	private int numberFiles;
	
	public Commit(Author author, Date date, String externalId) {
		super();
		this.author = author;
		this.date = date;
		this.externalId = externalId;
	}
	
	public Commit() {
	}
	
}
