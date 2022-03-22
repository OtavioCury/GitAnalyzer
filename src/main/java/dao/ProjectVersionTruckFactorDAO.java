package dao;

import javax.persistence.Query;

import enums.FileImportanceMetric;
import enums.KnowledgeMetric;
import enums.TruckFactorType;
import model.ProjectVersion;
import model.ProjectVersionTruckFactor;

public class ProjectVersionTruckFactorDAO extends GenericDAO<ProjectVersionTruckFactor>{

	@Override
	public ProjectVersionTruckFactor find(Object id) {
		return null;
	}

	@Override
	public boolean exist(ProjectVersionTruckFactor entity) {
		return false;
	}
	
	public boolean existsByProjectVersionTruckFactor(ProjectVersion projectVersion, 
			KnowledgeMetric knowledgeMetric, FileImportanceMetric fileImportanceMetric, TruckFactorType truckFactorType) {
		Query q = em.createQuery("select count(id) from ProjectVersionTruckFactor p "
				+ "where p.projectVersion.id=:idProjectVersion and p.knowledgeMetric=:knowledgeMetric "
				+ "and p.fileImportanceMetric=:fileImportanceMetric and p.truckFactorType=:truckFactorType");
		q.setParameter("idProjectVersion", projectVersion.getId());
		q.setParameter("knowledgeMetric", knowledgeMetric);
		q.setParameter("fileImportanceMetric", fileImportanceMetric);
		q.setParameter("truckFactorType", truckFactorType);
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}
	
	public ProjectVersionTruckFactor findByProjectVersionTruckFactor(ProjectVersion projectVersion, 
			KnowledgeMetric knowledgeMetric, FileImportanceMetric fileImportanceMetric, TruckFactorType truckFactorType) {
		Query q = em.createQuery("select p from ProjectVersionTruckFactor p "
				+ "where p.projectVersion.id=:idProjectVersion and p.knowledgeMetric=:knowledgeMetric "
				+ "and p.fileImportanceMetric=:fileImportanceMetric and p.truckFactorType=:truckFactorType");
		q.setParameter("idProjectVersion", projectVersion.getId());
		q.setParameter("knowledgeMetric", knowledgeMetric);
		q.setParameter("fileImportanceMetric", fileImportanceMetric);
		q.setParameter("truckFactorType", truckFactorType);
		try {
			ProjectVersionTruckFactor projectVersionTruckFactor = (ProjectVersionTruckFactor) q.getSingleResult();
			return projectVersionTruckFactor;
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
