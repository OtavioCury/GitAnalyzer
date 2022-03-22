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

	public Contributor findByNameEmailProject(String name, String email, Project project) {
		String hql = "select c from Contributor c where c.name=:name "
				+ "and c.email=:email and c.project.id=:idProject";
		Query q = em.createQuery(hql);
		q.setParameter("name", name);
		q.setParameter("email", email);
		q.setParameter("idProject", project.getId());
		try {
			return (Contributor) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public Contributor findByEmailProject(String email, Project project) {
		String hql = "select c from Contributor c where c.email=:email and c.project.id=:idProject";
		Query q = em.createQuery(hql);
		q.setParameter("email", email);
		q.setParameter("idProject", project.getId());
		List<Contributor> contributors = q.getResultList();
		if (contributors.size() > 0) {
			return contributors.get(0);
		}else {
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
