package dials;

import dials.execution.ExecutionContextRecorder;

import javax.persistence.EntityManager;

public class DialsSystemInitializer {

    private static DialsSystemInitializer initializer = new DialsSystemInitializer();

    private DialsSystemConfiguration configuration;

    private DialsSystemInitializer() {
        configuration = new DialsSystemConfiguration();
    }

    public static DialsSystemInitializer getInstance() {
        return initializer;
    }

    public DialsSystemInitializer withEntityManager(EntityManager entityManager) {
        configuration.setEntityManager(entityManager);
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

    public void initializeSystem() {
        if (configuration.validate()) {
            Dials.init(configuration);
        }
    }
}
