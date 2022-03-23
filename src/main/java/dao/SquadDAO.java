package dao;

import java.util.List;

import javax.persistence.Query;

import model.ProjectVersion;
import model.Squad;

public class SquadDAO extends GenericDAO<Squad>{

	@Override
	public Squad find(Object id) {
		return null;
	}

	@Override
	public boolean exist(Squad entity) {
		return false;
	}

	public boolean existsByNameProjectVersion(String name, ProjectVersion projectVersion) {
		Query q = em.createQuery("select count(id) from Squad s "
				+ "where s.projectVersion.id=:idProjectVersion and s.name=:name");
		q.setParameter("idProjectVersion", projectVersion.getId());
		q.setParameter("name", name);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public List<Squad> listByProjectVersion(ProjectVersion projectVersion) {
		Query q = em.createQuery("select s from Squad s where s.projectVersion.id=:idProjectVersion");
		q.setParameter("idProjectVersion", projectVersion.getId());
		List<Squad> squads = q.getResultList();
		return squads;
	}

}
