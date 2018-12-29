package pvt.hrk.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum PersistenceManager {

	INSTANCE;
	private static final EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("user1schema1");
	private static EntityManager em;
public static final int batchSize=50;
	public EntityManager getEntityManager() {
		if (em == null || !em.isOpen()) {
			em = emFactory.createEntityManager();
		}
		return em;
	}

	public void close() {
		if (em != null && em.isOpen()) {
			em.close();
		}
		if(emFactory != null && emFactory.isOpen()){
		emFactory.close();
		}
	}
}
