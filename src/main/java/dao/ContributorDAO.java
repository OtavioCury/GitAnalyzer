package dao;

import java.util.List;

import javax.persistence.Query;

import model.Contributor;
import model.Project;

public class ContributorDAO extends GenericDAO<Contributor>{

	@Override
	public Contributor find(Object id) {
		return null;
	}

	@Override
	public boolean exist(Contributor entity) {
		return false;
	}
	
	public Contributor findByNameEmail(String name, String email) {
		String hql = "select c from Contributor c where c.name=:name and c.email=:email";
		Query q = em.createQuery(hql);
		q.setParameter("name", name);
		q.setParameter("email", email);
		try {
			return (Contributor) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	
	public List<Contributor> findByProject(Project project){
		String hql = "select c from Contributor c where c.project.id=:idProject";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		return q.getResultList();
	}

}
