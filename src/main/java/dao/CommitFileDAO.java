package dao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import enums.OperationType;
import model.Commit;
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
		Query q = em.createQuery("select count(id) from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile and c.operation=:operation");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public boolean findByAuthorsFileAdd(List<Contributor> authors, File file) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select count(id) from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile and c.operation=:operation");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public boolean findLastDelByFileVersion(File file, Commit version) {
		String hql = "select max(cf.commit.date) from CommitFile cf where cf.file.id=:idFile and cf.operation=:operation and cf.commit.date<=:maxDate";
		Query q = em.createQuery(hql);
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.DEL);
		q.setParameter("maxDate", version.getDate());
		Date date = (Date) q.getSingleResult();
		if(date != null) {
			hql = "select count(id) from CommitFile cf where cf.file.id=:idFile and cf.commit.date > :date and cf.commit.date<=:maxDate";
			q = em.createQuery(hql);
			q.setParameter("idFile", file.getId());
			q.setParameter("date", date);
			q.setParameter("maxDate", version.getDate());
			boolean exists = (Long) q.getSingleResult() == 0;
			return exists;
		}else {
			return false;
		}
	}
	
	public Date findLastByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select max(c.commit.date) from CommitFile c "
				+ "where c.commit.author.id=:idAuthor and c.file.id=:idFile");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		Date date = (Date) q.getSingleResult();
		return date;
	}
	
	public Date findLastByAuthorsFileToVersion(List<Contributor> authors, File file, Commit version) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select max(c.commit.date) from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile and c.commit.date<=:maxDate");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		q.setParameter("maxDate", version.getDate());
		Date date = (Date) q.getSingleResult();
		return date;
	}
	
	public boolean existsByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select count(id) from CommitFile c "
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
	
	public List<CommitFile> findByAuthorsFileToVersion(List<Contributor> authors, File file, Commit version) {
		List<Long> ids = authors.stream().map(Contributor::getId).collect(Collectors.toList());
		Query q = em.createQuery("select c from CommitFile c "
				+ "where c.commit.author.id in (:ids) and c.file.id=:idFile and c.commit.date<=:maxDate");
		q.setParameter("ids", ids);
		q.setParameter("idFile", file.getId());
		q.setParameter("maxDate", version.getDate());
		return q.getResultList();		
	}
	
	public List<CommitFile> findAll(){
		return findAll(CommitFile.class);
	}

}
