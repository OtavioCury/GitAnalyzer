package dao;

import javax.persistence.Query;

import model.Contributor;
import model.AuthorFile;
import model.File;

public class AuthorFileDAO extends GenericDAO<AuthorFile>{

	@Override
	public AuthorFile find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(AuthorFile entity) {
		// TODO Auto-generated method stub
		return false;
	}

	public AuthorFile findByAuthorFile(Contributor author, File file) {
		String hql = "select a from AuthorFile a where a.author.id=:idAuthor and a.file.id=:idFile";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		try {
			return (AuthorFile) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
