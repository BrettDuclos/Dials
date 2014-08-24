package dials;

import com.hazelcast.hibernate.instance.HazelcastAccessor;
import dials.datastore.DialsMapStore;
import dials.datastore.DialsRepository;
import dials.execution.ExecutionContextRecorder;
import dials.execution.NoopExecutionContextRecorder;
import dials.filter.FeatureFilter;
import org.hibernate.Session;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DialsSystemConfiguration {

    private Logger logger = LoggerFactory.getLogger(DialsSystemConfiguration.class);

    private DialsRepository repository;
    private ExecutionContextRecorder executionContextRecorder;
    private boolean failFastEnabled = true;
    private Map<String, Class<? extends FeatureFilter>> availableFeatureFilters;

    private EntityManager entityManager;

    public DialsSystemConfiguration() {
        availableFeatureFilters = new HashMap<>();
    }

    public DialsSystemConfiguration withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return this;
    }


    public DialsSystemConfiguration withExecutionContextRecorder(ExecutionContextRecorder executionContextRecorder) {
        this.executionContextRecorder = executionContextRecorder;
        return this;
    }

    public DialsSystemConfiguration withFailFastEnabled(boolean failFastEnabled) {
        this.failFastEnabled = failFastEnabled;
        return this;
    }

    public void initializeSystem() {
        if (entityManager == null) {
            logger.error("A EntityManager is required to initialize the Dials system.");
            return;
        }

        DialsMapStore.setEntityManager(entityManager);
        repository = new DialsRepository(HazelcastAccessor.getHazelcastInstance(entityManager.unwrap(Session.class)));


        if (executionContextRecorder == null) {
            executionContextRecorder = new NoopExecutionContextRecorder();
        }

        Reflections reflections = new Reflections("dials");
        Set<Class<? extends FeatureFilter>> filters = reflections.getSubTypesOf(FeatureFilter.class);

        for (Class<? extends FeatureFilter> filter : filters) {
            String key = filter.getSimpleName().replace("FeatureFilter", "");
            availableFeatureFilters.put(key.toLowerCase(), filter);
            logger.info("Detected Feature Filter - " + key);
        }

        Dials.init(this);
    }

    public DialsRepository getRepository() {
        return repository;
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public ExecutionContextRecorder getExecutionContextRecorder() {
        return executionContextRecorder;
    }

    public boolean isFailFastEnabled() {
        return failFastEnabled;
    }

    public Map<String, Class<? extends FeatureFilter>> getAvailableFeatureFilters() {
        return availableFeatureFilters;
    }

}
