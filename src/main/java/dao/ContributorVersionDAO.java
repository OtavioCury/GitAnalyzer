package dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import model.Commit;
import model.Contributor;
import model.ContributorVersion;

public class ContributorVersionDAO extends GenericDAO<ContributorVersion>{

	@Override
	public ContributorVersion find(Object id) {
		return null;
	}

	@Override
	public boolean exist(ContributorVersion entity) {
		return false;
	}
	
	public boolean existsContributorVersion(Contributor contributor, Commit version) {
		String hql = "select count(id) from ContributorVersion cv where cv.contributor.id=:idContributor "
				+ "and cv.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idContributor", contributor.getId());
		q.setParameter("idVersion", version.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public boolean disabledContributorVersion(Contributor contributor, Commit version) {
		String hql = "select count(id) from ContributorVersion cv where cv.contributor.id=:idContributor "
				+ "and cv.version.id=:idVersion and cv.disabled=true";
		Query q = em.createQuery(hql);
		q.setParameter("idContributor", contributor.getId());
		q.setParameter("idVersion", version.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public List<ContributorVersion> activeContributorVersion(Commit version) {
		List<ContributorVersion> contributors = new ArrayList<ContributorVersion>();
		String hql = "select cv from ContributorVersion cv where cv.version.id=:idVersion and cv.disabled=false";
		Query q = em.createQuery(hql);
		q.setParameter("idVersion", version.getId());
		contributors = q.getResultList();
		return contributors;
	}
	
	public List<ContributorVersion> findByVersion(Commit version) {
		List<ContributorVersion> contributors = new ArrayList<ContributorVersion>();
		String hql = "select cv from ContributorVersion cv where cv.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idVersion", version.getId());
		contributors = q.getResultList();
		return contributors;
	}

}
