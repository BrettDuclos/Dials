package dials;

import com.codahale.metrics.MetricRegistry;
import dials.execution.ExecutionContextRecorder;

import javax.persistence.EntityManagerFactory;

public class DialsSystemInitializer {

    private static DialsSystemInitializer initializer = new DialsSystemInitializer();

    private DialsSystemConfiguration configuration;

    private DialsSystemInitializer() {
        configuration = new DialsSystemConfiguration();
    }

    public static DialsSystemInitializer getInstance() {
        return initializer;
    }

    public DialsSystemInitializer withEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        configuration.setEntityManagerFactory(entityManagerFactory);
        return this;
    }

    public DialsSystemInitializer withExecutionContextRecorder(ExecutionContextRecorder executionContextRecorder) {
        configuration.setExecutionContextRecorder(executionContextRecorder);
        return this;
    }

    public DialsSystemInitializer withFailFastEnabled(boolean failFastEnabled) {
        configuration.setFailFastEnabled(failFastEnabled);
        return this;
    }

    public DialsSystemInitializer withMetricRegistry(MetricRegistry metricRegistry) {
        configuration.setMetricRegistry(metricRegistry);
        return this;
    }

    public void initializeSystem() {
        if (configuration.validate()) {
            Dials.init(configuration);
        }
    }
}
