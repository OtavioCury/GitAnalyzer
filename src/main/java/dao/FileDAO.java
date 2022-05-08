package dao;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import model.Commit;
import model.Contributor;
import model.File;
import model.Project;

public class FileDAO extends GenericDAO<File>{

	@Override
	public File find(Object id) {
		return null;
	}

	@Override
	public boolean exist(File entity) {
		return false;
	}

	public boolean existsByFilePathProject(String path, Project project) {
		String hql = "select count(id) from File f where f.path=:path and f.project.id=:id";
		Query q = em.createQuery(hql);
		q.setParameter("path", path);
		q.setParameter("id", project.getId());
		boolean exists = (Long) q.getSingleResult() > 0;
		return exists;
	}

	public File findByPath(String path, Project project) {
		String hql = "select f from File f where f.path=:path and f.project.id=:id";
		Query q = em.createQuery(hql);
		q.setParameter("path", path);
		q.setParameter("id", project.getId());
		try {
			return (File) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public List<File> findByProject(Project project){
		String hql = "select f from File f where f.project.id=:idProject";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		return q.getResultList();
	}

	public List<File> findByProjectExtensions(Project project, List<String> extensions){
		String hql = "select f from File f where f.project.id=:idProject and f.extension in (:extensions)";
		Query q = em.createQuery(hql);
		q.setParameter("idProject", project.getId());
		q.setParameter("extensions", extensions);
		return q.getResultList();
	}

	public LinkedHashMap<File, Long> findOrderedMostCommited(Project project, List<File> files, 
			Commit currentCommit){
		List<Long> idsFiles = files.stream().map(File::getId).collect(Collectors.toList());
		LinkedHashMap<File, Long> fileCommits = new LinkedHashMap<File, Long>();
		String hql = "select count(*) as count_file, cf.file.path from CommitFile cf where cf.file.id in (:idFiles)"
				+ " and cf.commit.date <= :date group by cf.file.path order by count_file desc";
		Query q = em.createQuery(hql);
		q.setParameter("idFiles", idsFiles);
		q.setParameter("date", currentCommit.getDate());
		List<Object[]> objects = q.getResultList();
		for(Object[] object: objects) {
			Long numberCommits = (Long) object[0];
			File file = findByPath((String) object[1], project);
			fileCommits.put(file, numberCommits);
		}
		return fileCommits;
	}

	public LinkedHashMap<File, Long> findOrderedMostChanged(Project project, List<File> files){
		List<Long> idsFiles = files.stream().map(File::getId).collect(Collectors.toList());
		LinkedHashMap<File, Long> fileChanges = new LinkedHashMap<File, Long>();
		String hql = "select sum(cf.adds + cf.dels) as sum_changes, cf.file.path from CommitFile cf where "
				+ "cf.file.id in (:idFiles) group by cf.file.path order by sum_changes desc";
		Query q = em.createQuery(hql);
		q.setParameter("idFiles", idsFiles);
		List<Object[]> objects = q.getResultList();
		for(Object[] object: objects) {
			Long numberCommits = (Long) object[0];
			File file = findByPath((String) object[1], project);
			fileChanges.put(file, numberCommits);
		}
		return fileChanges;
	} 

	public Set<File> filesTouchedContributorsLastMonths(List<Contributor> contributors, Date date){
		List<Long> idsContributos = contributors.stream().map(Contributor::getId).collect(Collectors.toList());
		String hql = "select c.file from CommitFile c where c.commit.date >=:date and c.commit.author.id in (:idsContributors)";
		Query q = em.createQuery(hql);
		q.setParameter("date", date);
		q.setParameter("idsContributors", idsContributos);
		List<File> files = q.getResultList();
		Set<File> filesSet = new HashSet<File>(files);
		return filesSet;
	}

}