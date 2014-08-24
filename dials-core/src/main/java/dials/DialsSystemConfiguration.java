package dials;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.hazelcast.hibernate.instance.HazelcastAccessor;
import dials.datastore.DialsMapStore;
import dials.datastore.DialsRepository;
import dials.datastore.EntityManagerHelper;
import dials.execution.ExecutionContextRecorder;
import dials.execution.NoopExecutionContextRecorder;
import dials.filter.FeatureFilter;
import org.hibernate.Session;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DialsSystemConfiguration {

    private Logger logger = LoggerFactory.getLogger(DialsSystemConfiguration.class);

    private MetricRegistry metricRegistry;
    private JmxReporter metricReporter;
    private DialsRepository repository;
    private ExecutionContextRecorder executionContextRecorder;
    private Map<String, Class<? extends FeatureFilter>> availableFeatureFilters;
    private boolean failFastEnabled = true;

    public DialsSystemConfiguration() {
        availableFeatureFilters = new HashMap<>();

        Reflections reflections = new Reflections("dials");
        Set<Class<? extends FeatureFilter>> filters = reflections.getSubTypesOf(FeatureFilter.class);

        for (Class<? extends FeatureFilter> filter : filters) {
            String key = filter.getSimpleName().replace("FeatureFilter", "");
            availableFeatureFilters.put(key.toLowerCase(), filter);
            logger.info("Detected Feature Filter - " + key);
        }
    }

    protected boolean validate() {
        if (repository == null) {
            logger.error("An EntityManager is required to initialize the Dials system.");
            return false;
        }

        if (executionContextRecorder == null) {
            executionContextRecorder = new NoopExecutionContextRecorder();
        }

        if (metricRegistry == null) {
            metricRegistry = new MetricRegistry();
            metricReporter = JmxReporter.forRegistry(metricRegistry).inDomain("Dials").build();
            metricReporter.start();
        }

        return true;
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    protected void setMetricRegistry(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    public DialsRepository getRepository() {
        return repository;
    }

    public ExecutionContextRecorder getExecutionContextRecorder() {
        return executionContextRecorder;
    }

    protected void setExecutionContextRecorder(ExecutionContextRecorder executionContextRecorder) {
        this.executionContextRecorder = executionContextRecorder;
    }

    public boolean isFailFastEnabled() {
        return failFastEnabled;
    }

    protected void setFailFastEnabled(boolean failFastEnabled) {
        this.failFastEnabled = failFastEnabled;
    }

    public Map<String, Class<? extends FeatureFilter>> getAvailableFeatureFilters() {
        return availableFeatureFilters;
    }

    protected void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        DialsMapStore.setEntityManagerHelper(new EntityManagerHelper(entityManagerFactory));
        repository = new DialsRepository(HazelcastAccessor
                .getHazelcastInstance(entityManagerFactory.createEntityManager().unwrap(Session.class)));
    }
}
