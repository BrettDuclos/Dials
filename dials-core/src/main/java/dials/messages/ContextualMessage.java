package dials.messages;

import dials.DialsSystemConfiguration;
import dials.datastore.FeatureManipulationActions;
import dials.execution.ExecutionContext;
import dials.model.FeatureModel;

public class ContextualMessage implements FeatureManipulationActions {

    private ExecutionContext executionContext;
    private DialsSystemConfiguration configuration;

    public ContextualMessage(ExecutionContext executionContext, DialsSystemConfiguration configuration) {
        this.executionContext = executionContext;
        this.configuration = configuration;
    }

    public ContextualMessage(ContextualMessage message) {
        this(message.getExecutionContext(), message.getConfiguration());
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public DialsSystemConfiguration getConfiguration() {
        return configuration;
    }

    public FeatureModel getFeature() {
        return configuration.getRepository().getFeature(executionContext.getFeatureName());
    }


    @Override
    public void disableFeature(String featureName) {
        configuration.getRepository().disableFeature(featureName);
    }

    @Override
    public void registerFeatureAttempt(String featureName, boolean executed) {
        configuration.getRepository().registerFeatureAttempt(featureName, executed);
    }

    @Override
    public void registerFeatureError(String featureName) {
        configuration.getRepository().registerFeatureError(featureName);
    }

    @Override
    public void performDialAdjustment(String featureName, String filterName, String dataKey, String dataValue) {
        configuration.getRepository().performDialAdjustment(featureName, filterName, dataKey, dataValue);
    }
}
