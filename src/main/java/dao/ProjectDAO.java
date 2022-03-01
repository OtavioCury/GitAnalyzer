package dao;

import javax.persistence.Query;

import model.Project;

public class ProjectDAO extends GenericDAO<Project>{

	@Override
	public Project find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(Project entity) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Project findByName(String name) {
		String hql = "select p from Project p where p.name=:name";
		Query q = em.createQuery(hql);
		q.setParameter("name", name);
		try {
			return (Project) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
