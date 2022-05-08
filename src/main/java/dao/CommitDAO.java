package dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import enums.OperationType;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;

public class CommitDAO extends GenericDAO<Commit>{

	@Override
	public Commit find(Object id) {
		return null;
	}

	@Override
	public boolean exist(Commit entity) {
		return false;
	}

	public boolean findByAuthorFileAdd(Contributor author, File file) {
		Query q = em.createQuery("select count(c.id) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id=:idFile and cf.operation=:operation "
				+ "where c.author.id=:idAuthor");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		q.setParameter("operation", OperationType.ADD);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public boolean existsByAuthorFile(Contributor author, File file) {
		Query q = em.createQuery("select count(c.id) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id=:idFile "
				+ "where c.author.id=:idAuthor");
		q.setParameter("idAuthor", author.getId());
		q.setParameter("idFile", file.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;		
	}

	public int sumAddsByAuthorsFileToVersion(Set<Long> idsAuthors, Set<Long> idsFiles, Commit version) {
		Query q = em.createQuery("select sum(cf.adds) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id in (:idsFiles) "
				+ "where c.date <=:maxDate and c.author.id in (:idsAuthors)");
		q.setParameter("idsAuthors", idsAuthors);
		q.setParameter("idsFiles", idsFiles);
		q.setParameter("maxDate", version.getDate());
		Long sumAdds = null;
		sumAdds = (Long) q.getSingleResult();
		return sumAdds.intValue();		
	}

	public Date findLastByAuthorsFileToVersion(Set<Long> idsAuthors, Set<Long> idsFiles, Commit version) {
		Query q = em.createQuery("select max(c.date) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id in (:idsFiles) "
				+ "where c.date <=:maxDate and c.author.id in (:idsAuthors)");
		q.setParameter("idsFiles", idsFiles);
		q.setParameter("maxDate", version.getDate());	
		q.setParameter("idsAuthors", idsAuthors);
		Date date = (Date) q.getSingleResult();
		return date;
	}

	public int numberCommitsFileAuthorsVersion(Set<Long> idsAuthors, Set<Long> idsFiles, Commit version) {
		Query q = em.createQuery("select count(c.id) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id in (:idsFiles) "
				+ "where c.date <=:maxDate and c.author.id in (:idsAuthors)");
		q.setParameter("idsAuthors", idsAuthors);
		q.setParameter("idsFiles", idsFiles);
		q.setParameter("maxDate", version.getDate());
		Long numberCommits = (Long) q.getSingleResult();
		return numberCommits.intValue();
	}

	public int numberCommitsFileOthersAuthorsVersion(Set<Long> idsAuthors, Set<Long> idsFiles, Commit version) {
		Query q = em.createQuery("select count(c.id) from Commit c inner join CommitFile cf on c.id=cf.commit.id and cf.file.id in (:idsFiles) "
				+ "where c.date <=:maxDate and c.author.id not in (:idsAuthors)");
		q.setParameter("idsAuthors", idsAuthors);
		q.setParameter("idsFiles", idsFiles);
		q.setParameter("maxDate", version.getDate());
		Long numberCommits = (Long) q.getSingleResult();
		return numberCommits.intValue();
	}

	public boolean findByAuthorsFileAdd(Set<Long> idsAuthors, Set<Long> idsFiles) {
		Query q = em.createQuery("select count(c.id) from Commit c "
				+ "inner join CommitFile cf on c.id=cf.commit.id and cf.operation=:operation and cf.file.id in (:idsFiles) where c.author.id in (:idsAuthors)");
		q.setParameter("idsAuthors", idsAuthors);
		q.setParameter("idsFiles", idsFiles);
		q.setParameter("operation", OperationType.ADD);
		boolean	exists = (Long) q.getSingleResult() > 0;
		return exists;
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

	public boolean findByIdExistsByProject(String id, Project project) {
		String hql = "select count(id) from Commit c where c.externalId=:id and c.project.id = :idProject";
		Query q = em.createQuery(hql);
		q.setParameter("id", id);
		q.setParameter("idProject", project.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public Date findLastCommitByContributorUpToVersion(Long contributorId, Commit currentVersion) {
		String hql = "select max(c.date) from Commit c where "
				+ "(c.author.id=:idContributor or c.commiter.id=:idContributor) and c.date <= :date";
		Query q = em.createQuery(hql);
		q.setParameter("idContributor", contributorId);
		q.setParameter("date", currentVersion.getDate());
		Date date = (Date) q.getSingleResult(); 
		return date;
	}

}
