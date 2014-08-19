package dials.datastore;

import dials.dial.Dial;
import dials.filter.FeatureFilter;
import dials.filter.FeatureFilterDataBean;

public interface DataStore {

    int MINIMUM_EXECUTION_COUNT = 50;

    boolean isDataStoreAccessible();

    /**
     * Validate the existence of a given feature.
     */
    boolean doesFeatureExist(String featureName);

    /**
     * Retrieves a filter definition, including static data, for a given feature.
     */
    FeatureFilterDataBean getFiltersForFeature(String featureName);

    /**
     * Validate the enablement of a given feature.
     */
    boolean isFeatureEnabled(String featureName);

    /**
     * Register an attempt for a given feature execution. If the feature was executed, also register the execution.
     */
    void registerAttempt(String featureName, boolean executed);

    /**
     * Register an error for a given feature execution.
     */
    void registerError(String featureName);

    /**
     * Get CountTuple instance of a given feature execution. Requires the execution and error counts.
     */
    CountTuple getExecutionCountTuple(String featureName);

    /**
     * For a given feature filter, update the value for the specified key.
     */
    void updateStaticData(Integer featureFilterId, String key, String newValue);

    /**
     * Get an instance of Dial as represented by the feature filter dial.
     */
    Dial getFilterDial(String featureName, FeatureFilter filter);

    /**
     * Register an attempt at filter manipulation based on dial configuration.
     */
    void registerDialAttempt(Integer featureFilterId);
}
