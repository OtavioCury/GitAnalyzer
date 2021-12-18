package dao;

import javax.persistence.Query;

import model.Commit;
import model.File;
import model.FileVersion;

public class FileVersionDAO extends GenericDAO<FileVersion>{

	@Override
	public FileVersion find(Object id) {
		return null;
	}

	@Override
	public boolean exist(FileVersion entity) {
		return false;
	}

	public boolean existsByFileVersion(File file, Commit commit) {
		Query q = em.createQuery("select count(id) from FileVersion f "
				+ "where f.file.id=:idFile and f.version.id=:idCommit");
		q.setParameter("idFile", file.getId());
		q.setParameter("idCommit", commit.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public FileVersion findByFileVersion(File file, Commit commit) {
		Query q = em.createQuery("select f from FileVersion f "
				+ "where f.file.id=:idFile and f.version.id=:idCommit");
		q.setParameter("idFile", file.getId());
		q.setParameter("idCommit", commit.getId());
		try {
			FileVersion FileVersion = (FileVersion) q.getSingleResult();
			return FileVersion;
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public int numberLinesFileVersion(File file, Commit commit) {
		Query q = em.createQuery("select f.numberLines from FileVersion f "
				+ "where f.file.id=:idFile and f.version.id=:idCommit");
		q.setParameter("idFile", file.getId());
		q.setParameter("idCommit", commit.getId());
		int numberLines = (int) q.getSingleResult();
		return numberLines;
	}

}
