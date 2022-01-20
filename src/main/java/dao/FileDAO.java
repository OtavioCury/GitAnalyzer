package dao;

import java.util.LinkedHashMap;
import java.util.List;

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
	
	public List<File> findByProject(Project project){
		String hql = "select f from File f where f.project.id=:idProject";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		return q.getResultList();
	}
	
	public List<File> findByProjectExtensions(Project project, List<String> extensions){
		String hql = "select f from File f where f.project.id=:idProject and f.extension in (:extensions)";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		q.setParameter("extensions", extensions);
		return q.getResultList();
	}
	
	public LinkedHashMap<File, Long> findOrderedMostCommited(Project project, List<String> extensions){
		LinkedHashMap<File, Long> fileCommits = new LinkedHashMap<File, Long>();
		String hql = "select count(*) as count_file, cf.file.path from CommitFile cf where cf.file.extension in (:extensions) "
				+ "and cf.file.project.id=:idProject group by cf.file.path order by count_file desc";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		q.setParameter("extensions", extensions);
		List<Object[]> objects = q.getResultList();
		for(Object[] object: objects) {
			Long numberCommits = (Long) object[0];
			File file = findByPath((String) object[1], project);
			fileCommits.put(file, numberCommits);
		}
		return fileCommits;
	}
	
}