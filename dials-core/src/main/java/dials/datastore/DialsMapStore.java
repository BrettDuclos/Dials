package dials.datastore;

import com.hazelcast.core.MapStore;
import dials.model.FeatureModel;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

public class DialsMapStore implements MapStore<String, FeatureModel> {

    private static EntityManager entityManager;

    @Override
    public void store(String key, FeatureModel value) {
        entityManager.getTransaction().begin();
        entityManager.merge(value);
        entityManager.getTransaction().commit();
    }

    @Override
    public void storeAll(Map<String, FeatureModel> map) {
        entityManager.getTransaction().begin();

        for (FeatureModel feature : map.values()) {
            entityManager.merge(feature);
        }

        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(String key) {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("delete from FeatureModel where featureName = :featureName")
                .setParameter("featureName", key);
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("delete from FeatureModel");
        query.executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Override
    public FeatureModel load(String key) {
        TypedQuery<FeatureModel> query =
                entityManager.createQuery("from FeatureModel where feature_name = :featureName", FeatureModel.class)
                        .setParameter("featureName", key).setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Map<String, FeatureModel> loadAll(Collection<String> keys) {
        Map<String, FeatureModel> models = new HashMap<>();

        TypedQuery<FeatureModel> query = entityManager.createQuery("from FeatureModel", FeatureModel.class);
        for (FeatureModel feature : query.getResultList()) {
            models.put(feature.getFeatureName(), feature);
        }

        return models;
    }

    @Override
    public Set<String> loadAllKeys() {
        Set<String> keys = new HashSet<>();
        TypedQuery<String> query = entityManager.createQuery("select featureName from FeatureModel", String.class);
        keys.addAll(query.getResultList());

        return keys;
    }

    public static void setEntityManager(EntityManager em) {
        entityManager = em;
    }

}
