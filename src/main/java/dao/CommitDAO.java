package dao;

import java.util.List;

import javax.persistence.Query;

import model.Commit;

public class CommitDAO extends GenericDAO<Commit>{

	@Override
	public Commit find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(Commit entity) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Commit findById(String id) {
		String hql = "select c from Commit c where c.externalId=:id";
		Query q = em.createQuery(hql);
		q.setParameter("id", id);
		try {
			return (Commit) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public List<Commit> commitsDescDate() {
		String hql = "select c from Commit c order by c.date desc";
		Query q = em.createQuery(hql);
		return q.getResultList();
	}

}
