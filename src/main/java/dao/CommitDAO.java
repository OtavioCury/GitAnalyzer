package dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import model.Commit;
import model.Contributor;

public class CommitDAO extends GenericDAO<Commit>{

	@Override
	public Commit find(Object id) {
		return null;
	}

	@Override
	public boolean exist(Commit entity) {
		return false;
	}

	public Commit findLastCommit() {
		String hql = "select c from Commit c order by c.date desc";
		Query q = em.createQuery(hql);
		q.setMaxResults(1);
		try {
			return (Commit) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
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

	public List<Commit> commitsByAuthor(Contributor contributor){
		String hql = "select c from Commit c where c.author.id=:authorId";
		Query q = em.createQuery(hql);
		q.setParameter("authorId", contributor.getId());
		return q.getResultList();
	}

	public boolean findByIdExists(String id) {
		String hql = "select count(id) from Commit c where c.externalId=:id";
		Query q = em.createQuery(hql);
		q.setParameter("id", id);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public Date findLastCommitByContributorUpToVersion(Contributor contributor, Commit currentVersion) {
		String hql = "select max(c.date) from Commit c where "
				+ "(c.author.id=:idContributor or c.commiter.id=:idContributor) and c.date <= :date";
		Query q = em.createQuery(hql);
		q.setParameter("idContributor", contributor.getId());
		q.setParameter("date", currentVersion.getDate());
		Date date = (Date) q.getSingleResult(); 
		return date;
	}

}
