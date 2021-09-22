package model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Commit {
	
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private Author author;
	private Date date;
	private String externalId;
	private int numberFilesMod;
	
	public Commit(Author author, Date date, String externalId, int numberFilesMod) {
		super();
		this.author = author;
		this.date = date;
		this.externalId = externalId;
		this.numberFilesMod = numberFilesMod;
	}
	
	public Commit() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public int getNumberFilesMod() {
		return numberFilesMod;
	}

	public void setNumberFilesMod(int numberFilesMod) {
		this.numberFilesMod = numberFilesMod;
	}
	
}
