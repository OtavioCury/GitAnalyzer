package model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import enums.OperationType;

@Entity
public class CommitFile {
	
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private File file;
	@ManyToOne
	private Commit commit;
	@Enumerated(EnumType.STRING)
	private OperationType operation;
	private int adds;
	private int dels;
	private int mods;
	private int amount;
	
	public CommitFile(File file, OperationType operation) {
		super();
		this.file = file;
		this.operation = operation;
	}
	
	public CommitFile() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	public int getAdds() {
		return adds;
	}

	public void setAdds(int adds) {
		this.adds = adds;
	}

	public int getDels() {
		return dels;
	}

	public void setDels(int dels) {
		this.dels = dels;
	}

	public int getMods() {
		return mods;
	}

	public void setMods(int mods) {
		this.mods = mods;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
