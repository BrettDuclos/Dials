package dials.datastore;

import dials.dial.Dial;
import dials.filter.FeatureFilter;
import dials.filter.FeatureFilterDataBean;

public interface DataStore {

    FeatureFilterDataBean getFiltersForFeature(String featureName);

    boolean doesFeatureExist(String featureName);

    boolean isFeatureEnabled(String featureName);

    void registerAttempt(String featureName, boolean executed);

    void registerError(String featureName);

    CountTuple getExecutionCountTuple(String featureName);

    void updateStaticData(Integer featureFilterId, String dial, String newValue);

    Dial getFilterDial(String featureName, FeatureFilter filter);

    void registerDialAttempt(Integer featureFilterId);
}
