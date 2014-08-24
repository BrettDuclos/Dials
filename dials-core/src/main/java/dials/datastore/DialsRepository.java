package dials.datastore;

import com.hazelcast.core.HazelcastInstance;
import dials.model.FeatureModel;
import dials.model.FilterModel;

import java.util.Map;

public class DialsRepository implements FeatureManipulationActions {

    private Map<String, FeatureModel> featureMap;

    public DialsRepository(HazelcastInstance hazelcast) {
        featureMap = hazelcast.getMap("featureMap");
    }

    public FeatureModel getFeature(String featureName) {
        return featureMap.get(featureName);
    }

    public void putFeature(FeatureModel featureModel) {
        featureMap.put(featureModel.getFeatureName(), featureModel);
    }

    @Override
    public void disableFeature(String featureName) {
        FeatureModel feature = getFeature(featureName);
        feature.setIsEnabled(false);
        putFeature(feature);
    }

    @Override
    public void registerFeatureAttempt(String featureName, boolean executed) {
        FeatureModel feature = getFeature(featureName);
        feature.getExecution().registerAttempt(executed);
        putFeature(feature);
    }

    @Override
    public void registerFeatureError(String featureName) {
        FeatureModel feature = getFeature(featureName);
        feature.getExecution().registerError();
        putFeature(feature);
    }

    @Override
    public void performDialAdjustment(String featureName, String filterName, String dataKey, String dataValue) {
        FeatureModel feature = getFeature(featureName);
        FilterModel filter = feature.getFilter(filterName);
        filter.updateStaticData(dataKey, dataValue);
        filter.getDial().registerAttempt();
        putFeature(feature);
    }

}
