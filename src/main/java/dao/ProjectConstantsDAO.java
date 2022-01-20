package dao;

import javax.persistence.Query;

import model.Project;
import model.ProjectConstants;

public class ProjectConstantsDAO extends GenericDAO<ProjectConstants>{

	@Override
	public ProjectConstants find(Object id) {
		return null;
	}

	@Override
	public boolean exist(ProjectConstants entity) {
		return false;
	}
	
	public ProjectConstants findByProject(Project project) {
		String hql = "select p from ProjectConstants p where p.project.id=:projectId";
		Query q = em.createQuery(hql);
		q.setParameter("projectId", project.getId());
		try {
			return (ProjectConstants) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
