package dao;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import model.Commit;
import model.File;
import model.FileRename;

public class FileRenameDAO extends GenericDAO<FileRename>{

	@Override
	public FileRename find(Object id) {
		return null;
	}

	@Override
	public boolean exist(FileRename entity) {
		return false;
	}
	
	public boolean findByLastFileCommit(File file, Commit commit){
		String hql = "select max(fr.commitChange.date) from FileRename fr where fr.oldFile.id=:idFile and fr.commitChange.date<=:maxDate";
		Query q = em.createQuery(hql);
		q.setParameter("idFile", file.getId());
		q.setParameter("maxDate", commit.getDate());
		Date date = (Date) q.getSingleResult();
		if(date != null) {
			hql = "select count(id) from CommitFile c where c.file.id=:idFile and c.commit.date > :date and c.commit.date<=:maxDate";
			q = em.createQuery(hql);
			q.setParameter("idFile", file.getId());
			q.setParameter("date", date);
			q.setParameter("maxDate", commit.getDate());
			boolean exists = (Long) q.getSingleResult() == 0;
			return exists;
		}else {
			return false;
		}
	}
	
	public List<File> findByFile(Set<File> files, Commit commit) {
		List<Long> idFiles = files.stream().map(File::getId).collect(Collectors.toList());
		String hql = "select fr.oldFile from FileRename fr where fr.newFile.id in (:idFiles) and fr.commitChange.date<=:maxDate";
		Query q = em.createQuery(hql);
		q.setParameter("idFiles", idFiles);
		q.setParameter("maxDate", commit.getDate());
		return q.getResultList();
	}

}
