package dao;

import javax.persistence.Query;

import enums.OperationType;
import model.Commit;
import model.File;
import model.FileCommit;

public class FileCommitDAO extends GenericDAO<FileCommit>{

	@Override
	public FileCommit find(Object id) {
		return null;
	}

	@Override
	public boolean exist(FileCommit entity) {
		return false;
	}
	
	public boolean findByFileCommit(File file, Commit commit) {
		Query q = em.createQuery("select count(*) from FileCommit f "
				+ "where f.file.id=:idFile and f.commit.id=:idCommit");
		q.setParameter("idFile", file.getId());
		q.setParameter("idCommit", commit.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

}
