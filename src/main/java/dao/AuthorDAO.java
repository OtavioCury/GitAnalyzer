package dao;

import javax.persistence.Query;

import model.Author;

public class AuthorDAO extends GenericDAO<Author>{

	@Override
	public Author find(Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exist(Author entity) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Author findByNameEmail(String name, String email) {
		String hql = "select a from Author a where a.name=:name and a.email=:email";
		Query q = em.createQuery(hql);
		q.setParameter("name", name);
		q.setParameter("email", email);
		try {
			return (Author) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

}
