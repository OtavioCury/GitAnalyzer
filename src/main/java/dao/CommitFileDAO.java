package dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import enums.OperationType;
import model.Commit;
import model.CommitFile;
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

	public List<Long> numberOfFileUpToCommit(Commit commit){
		String hql = "select count(*) as number_files from CommitFile cf where cf.commit.date <=:commitDate "
				+ "group by cf.commit.id order by number_files";
		Query q = em.createQuery(hql);
		q.setParameter("commitDate", commit.getDate());
		return q.getResultList();
	}

	public List<CommitFile> findByCommit(Commit commit){
		String hql = "select c from CommitFile c where c.commit.id=:commitId";
		Query q = em.createQuery(hql);
		q.setParameter("commitId", commit.getId());
		return q.getResultList();
	}

	public boolean existsByCommitFile(String id, String path) {
		String hql = "select count(c.id) from CommitFile c where "
				+ "c.commit.externalId=:id and c.file.path=:path";
		Query q = em.createQuery(hql);
		q.setParameter("id", id);
		q.setParameter("path", path);
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

}
