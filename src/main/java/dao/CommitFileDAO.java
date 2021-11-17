package dao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import enums.OperationType;
import model.CommitFile;
import model.Contributor;
import model.File;

public class CommitFileDAO extends GenericDAO<CommitFile>{

	@Override
	public CommitFile find(Object id) {
		return null;
	}

	@Override
	public boolean exist(CommitFile entity) {
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
	
	public boolean findByAuthorFileAdd(Contributor author, File file) {
		Query q = em.createQuery("select count(*) from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile and c.operation=:operation");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public boolean findByAuthorsFileAdd(List<Contributor> authors, File file) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select count(*) from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile and c.operation=:operation");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public Date findLastByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select max(c.commit.date) from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		Date date = (Date) q.getSingleResult();
		return date;
	}
	
	public Date findLastByAuthorsFile(List<Contributor> authors, File file) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select max(c.commit.date) from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		Date date = (Date) q.getSingleResult();
		return date;
	}
	
	public boolean existsByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select count(*) from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;		
	}
	
	public List<CommitFile> findByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select c from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		return q.getResultList();		
	}
	
	public List<CommitFile> findByAuthorsFile(List<Contributor> authors, File file) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select c from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		return q.getResultList();		
	}
	
	public List<CommitFile> findAll(){
		return findAll(CommitFile.class);
	}

}
