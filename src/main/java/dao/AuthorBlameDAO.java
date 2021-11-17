package dao;

import javax.persistence.Query;

import model.AuthorBlame;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;

public class AuthorBlameDAO extends GenericDAO<AuthorBlame>{

	@Override
	public AuthorBlame find(Object id) {
		return null;
	}

	@Override
	public boolean exist(AuthorBlame entity) {
		return false;
	}
	
	public AuthorBlame findByAuthorVersion(AuthorFile authorFile, Commit version) {
		String hql = "select a from AuthorBlame a where a.authorFile.id=:idAuthorFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthorFile", authorFile.getId());
		q.setParameter("idVersion", version.getId());
		try {
			return (AuthorBlame) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public boolean existsByAuthorVersion(AuthorFile authorFile, Commit version) {
		String hql = "select count(*) from AuthorBlame a "
				+ "where a.authorFile.id=:idAuthorFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthorFile", authorFile.getId());
		q.setParameter("idVersion", version.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

}
