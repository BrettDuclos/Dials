package dials.datastore;

import com.hazelcast.core.MapStore;
import dials.model.FeatureModel;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

public class DialsMapStore implements MapStore<String, FeatureModel> {

    private static EntityManagerHelper entityManagerHelper;

    @Override
    public void store(String key, FeatureModel value) {
        entityManagerHelper.getEntityManager().getTransaction().begin();
        entityManagerHelper.getEntityManager().merge(value);
        entityManagerHelper.getEntityManager().getTransaction().commit();
    }

    @Override
    public void storeAll(Map<String, FeatureModel> map) {
        entityManagerHelper.getEntityManager().getTransaction().begin();

        for (FeatureModel feature : map.values()) {
            entityManagerHelper.getEntityManager().merge(feature);
        }

        entityManagerHelper.getEntityManager().getTransaction().commit();
    }

    @Override
    public void delete(String key) {
        entityManagerHelper.getEntityManager().getTransaction().begin();

        Query query = entityManagerHelper.getEntityManager()
                .createQuery("delete from FeatureModel where featureName = :featureName")
                .setParameter("featureName", key);

        query.executeUpdate();

        entityManagerHelper.getEntityManager().getTransaction().commit();
    }

    @Override
    public void deleteAll(Collection<String> keys) {
        entityManagerHelper.getEntityManager().getTransaction().begin();

        Query query = entityManagerHelper.getEntityManager()
                .createQuery("delete from FeatureModel");

        query.executeUpdate();

        entityManagerHelper.getEntityManager().getTransaction().commit();
    }

    @Override
    public FeatureModel load(String key) {
        TypedQuery<FeatureModel> query =
                entityManagerHelper.getEntityManager()
                        .createQuery("from FeatureModel where feature_name = :featureName", FeatureModel.class)
                        .setParameter("featureName", key)
                        .setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Map<String, FeatureModel> loadAll(Collection<String> keys) {
        Map<String, FeatureModel> models = new HashMap<>();

        TypedQuery<FeatureModel> query = entityManagerHelper.getEntityManager()
                .createQuery("from FeatureModel", FeatureModel.class);

        for (FeatureModel feature : query.getResultList()) {
            models.put(feature.getFeatureName(), feature);
        }

        return models;
    }

    @Override
    public Set<String> loadAllKeys() {
        Set<String> keys = new HashSet<>();

        TypedQuery<String> query = entityManagerHelper.getEntityManager()
                .createQuery("select featureName from FeatureModel", String.class);

        keys.addAll(query.getResultList());

        return keys;
    }

    public static void setEntityManagerHelper(EntityManagerHelper helper) {
        entityManagerHelper = helper;
    }

}
