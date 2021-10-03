package dao;

import java.util.List;

import javax.persistence.Query;

import enums.OperationType;
import model.CommitFile;
import model.Contributor;
import model.File;

public class CommitFileDAO extends GenericDAO<CommitFile>{

	@Override
	public CommitFile find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(CommitFile entity) {
		// TODO Auto-generated method stub
		return false;
	}

	public CommitFile findByCommitFile(String id, String path) {
		String hql = "select c from CommitFile c where "
				+ "c.commit.externalId=:id and c.file.path=:path";
		Query q = em.createQuery(hql);
		q.setParameter("id", id);
		q.setParameter("path", path);
		try {
			return (CommitFile) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public CommitFile findByAuthorFileAdd(Contributor author, File file) {
		String hql = "select c from CommitFile c where "
				+ "c.commit.author.id=:idAuthor and c.file.id=:idFile and c.operation=:operation";
		Query q = em.createQuery(hql);
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD.getOperationType());
		try {
			return (CommitFile) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
	
	public List<CommitFile> findAll(){
		return findAll(CommitFile.class);
	}

}
