package dials.datastore;

import dials.filter.FeatureFilterDataBean;

public interface DataStore {

    FeatureFilterDataBean getFiltersForFeature(String featureName);

    boolean doesFeatureExist(String featureName);

    boolean isFeatureEnabled(String featureName);

    void registerAttempt(String featureName, boolean executed);

    void registerError(String featureName);

    CountTuple getExecutionCountTuple(String featureName);

    void updateStaticData(String featureName, String dial, String newValue);
}
