package dao;

import java.util.List;

import javax.persistence.Query;

import model.Commit;
import model.File;
import model.FileOtherPath;

public class FileOtherPathDAO extends GenericDAO<FileOtherPath>{

	@Override
	public FileOtherPath find(Object id) {
		return null;
	}

	@Override
	public boolean exist(FileOtherPath entity) {
		return false;
	}
	
	public FileOtherPath findByPathFileCommit(String path, File file, Commit commit) {
		String hql = "select f from FileOtherPath f where f.path=:path and f.file.id=:file and f.commitChange.id=:commit";
		Query q = em.createQuery(hql);
		q.setParameter("path", path);
		q.setParameter("file", file.getId());
		q.setParameter("commit", commit.getId());
		try {
			return (FileOtherPath) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public FileOtherPath findByPaths(List<String> paths) {
		String hql = "select f from FileOtherPath f where f.path in (:paths)";
		Query q = em.createQuery(hql);
		q.setParameter("paths", paths);
		try {
			return (FileOtherPath) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
