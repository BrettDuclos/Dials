package dials.datastore;

public interface FeatureManipulationActions {

    void disableFeature(String featureName);

    void registerFeatureAttempt(String featureName, boolean executed);

    void registerFeatureError(String featureName);

    void performDialAdjustment(String featureName, String filterName, String dataKey, String dataValue);

}
