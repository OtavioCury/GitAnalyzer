package dao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import model.AuthorDOA;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;

public class AuthorDoaDAO extends GenericDAO<AuthorDOA>{

	@Override
	public AuthorDOA find(Object id) {
		return null;
	}

	@Override
	public boolean exist(AuthorDOA entity) {
		return false;
	}

	public Contributor maxDoaByFileVersion(File file, Commit version, Set<Contributor> contributors) {
		List<Long> ids = contributors.stream().map(Contributor::getId).collect(Collectors.toList());
		String hql = "select a.authorFile.author from AuthorDOA a "
				+ "where a.authorFile.file.id=:idFile and a.version.id=:idVersion";
		if(ids.size() > 0) {
			hql = hql + " and a.authorFile.author.id not in (:ids)";
		}
		hql = hql + " order by a.degreeOfAuthorship desc";
		Query q = em.createQuery(hql);
		q.setParameter("idFile", file.getId());
		q.setParameter("idVersion", version.getId());
		if(ids.size() > 0) {
			q.setParameter("ids", ids);
		}
		q.setMaxResults(1);
		return (Contributor) q.getSingleResult();
	} 

	public AuthorDOA findByAuthorVersion(AuthorFile authorFile, Commit version) {
		String hql = "select a from AuthorDOA a where a.authorFile.id=:idAuthorFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthorFile", authorFile.getId());
		q.setParameter("idVersion", version.getId());
		try {
			return (AuthorDOA) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public boolean existsByAuthorVersion(AuthorFile authorFile, Commit version) {
		String hql = "select count(id) from AuthorDOA a "
				+ "where a.authorFile.id=:idAuthorFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthorFile", authorFile.getId());
		q.setParameter("idVersion", version.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public List<AuthorDOA> findByFileVersion(File file, Commit version) {
		String hql = "select a from AuthorDOA a where a.authorFile.file.id=:idFile and a.version.id=:idVersion";
		Query q = em.createQuery(hql);
		q.setParameter("idFile", file.getId());
		q.setParameter("idVersion", version.getId());
		return q.getResultList();
	}

}
