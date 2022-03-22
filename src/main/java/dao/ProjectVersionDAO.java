package dao;

import javax.persistence.Query;

import model.Commit;
import model.Project;
import model.ProjectVersion;

public class ProjectVersionDAO extends GenericDAO<ProjectVersion>{

	@Override
	public ProjectVersion find(Object id) {
		return null;
	}

	@Override
	public boolean exist(ProjectVersion entity) {
		return false;
	}
	
	public boolean existsByProjectVersion(Project project, Commit commit) {
		Query q = em.createQuery("select count(id) from ProjectVersion p "
				+ "where p.project.id=:idFile and p.version.id=:idCommit");
		q.setParameter("idFile", project.getId());
		q.setParameter("idCommit", commit.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public ProjectVersion findByProjectVersion(Project project, Commit commit) {
		Query q = em.createQuery("select p from ProjectVersion p "
				+ "where p.project.id=:idFile and p.version.id=:idCommit");
		q.setParameter("idFile", project.getId());
		q.setParameter("idCommit", commit.getId());
		try {
			ProjectVersion projectVersion = (ProjectVersion) q.getSingleResult();
			return projectVersion;
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}
}
