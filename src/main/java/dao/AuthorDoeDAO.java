package dao;

import javax.persistence.Query;

import model.AuthorDOE;
import model.AuthorFile;
import model.Commit;

public class AuthorDoeDAO extends GenericDAO<AuthorDOE>{

	@Override
	public AuthorDOE find(Object id) {
		return null;
	}

	@Override
	public boolean exist(AuthorDOE entity) {
		return false;
	}
	
	public AuthorDOE findByAuthorVersion(AuthorFile authorFile, Commit version) {
		String hql = "select a from AuthorDOE a where a.authorFile.id=:idAuthorFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthorFile", authorFile.getId());
		q.setParameter("idVersion", version.getId());
		try {
			return (AuthorDOE) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
