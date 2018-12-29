package pvt.hrk.database;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public class GenericDAO<T, ID extends Serializable> {

	private static final String SELECT_ALL = "Select t from %s t";
	private static final String SELECT_COUNT = "SELECT COUNT(t) FROM %s t";
	private Class<T> persistentClass;

	public GenericDAO(Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}

	public void save(T t) {
		EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();

	}

	public void saveAll(List<T> tt) {
		EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
		em.getTransaction().begin();
		int[] i = { 0 };
		for(T t:tt){
			em.persist(t);
			if (i[0]++ % PersistenceManager.batchSize == 0) {
				em.flush();
				em.clear();
			}
		}
		em.flush();
		em.clear();
		em.getTransaction().commit();

	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
		return em.createQuery(String.format(SELECT_ALL, persistentClass.getSimpleName())).getResultList();
	}

	public T findById(ID id) {
		EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
		return em.find(persistentClass, id);

	}

}
