package model;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
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
	private Contributor author;
	@ManyToOne
	private Contributor commiter;
	@ElementCollection
    private List<String> parentsIds;
	private Date date;
	private String externalId;
	private int numberFilesMod;
	
	public Commit(Contributor author, Contributor commiter, Date date, String externalId, int numberFilesMod, List<String> parentsIds) {
		super();
		this.author = author;
		this.commiter = commiter;
		this.date = date;
		this.externalId = externalId;
		this.numberFilesMod = numberFilesMod;
		this.parentsIds = parentsIds;
	}
	
	public Commit() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Contributor getAuthor() {
		return author;
	}

	public void setAuthor(Contributor author) {
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

	public List<String> getParentsIds() {
		return parentsIds;
	}

	public void setParentsIds(List<String> parentsIds) {
		this.parentsIds = parentsIds;
	}

	public Contributor getCommiter() {
		return commiter;
	}

	public void setCommiter(Contributor commiter) {
		this.commiter = commiter;
	}
	
}
