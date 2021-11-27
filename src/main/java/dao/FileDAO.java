package dao;

import javax.persistence.Query;

import model.File;
import model.Project;

public class FileDAO extends GenericDAO<File>{

	@Override
	public File find(Object id) {
		return null;
	}

	@Override
	public boolean exist(File entity) {
		return false;
	}
	
	public File findByPath(String path, Project project) {
		String hql = "select f from File f where f.path=:path and f.project.id=:id";
		Query q = em.createQuery(hql);
		q.setParameter("path", path);
		q.setParameter("id", project.getId());
		try {
			return (File) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
}
