package dials.datastore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EntityManagerHelper {

    private final EntityManagerFactory entityManagerFactory;
    private final ThreadLocal<EntityManager> threadLocal;

    public EntityManagerHelper(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        threadLocal = new ThreadLocal<>();
    }

    public EntityManager getEntityManager() {
        EntityManager entityManager = threadLocal.get();

        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
            threadLocal.set(entityManager);
        }
        return entityManager;
    }

    public void closeEntityManager() {
        EntityManager entityManager = threadLocal.get();

        if (entityManager != null) {
            entityManager.close();
            threadLocal.set(null);
        }
    }

    public void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }

    public void beginTransaction() {
        getEntityManager().getTransaction().begin();
    }

    public void rollback() {
        getEntityManager().getTransaction().rollback();
    }

    public void commit() {
        getEntityManager().getTransaction().commit();
    }
}
