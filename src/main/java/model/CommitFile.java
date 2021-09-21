package model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import enums.OperationType;
import lombok.Data;

@Entity
@Data
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
}
