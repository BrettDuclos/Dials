package dials.datastore;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import dials.model.FeatureExecutionModel;
import dials.model.FeatureModel;
import dials.model.FilterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class DialsRepository implements FeatureManipulationActions {

    private Logger logger = LoggerFactory.getLogger(DialsRepository.class);

    private IMap<String, FeatureModel> featureMap;

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
        if (feature != null) {
            getExecution(feature).registerAttempt(executed);
            putFeature(feature);
        }
    }

    @Override
    public void registerFeatureError(String featureName) {
        FeatureModel feature = getFeature(featureName);
        if (feature != null) {
            FeatureExecutionModel execution = getExecution(feature);
            execution.registerError();
            CountTuple tuple = new CountTuple(execution.getExecutions(), execution.getErrors());

            if (tuple.getRateOfSuccess().compareTo(new BigDecimal(feature.getKillswitchThreshold())) < 0) {
                logger.warn("Feature fell below Killswitch Threshold. Disabling.");
                feature.setIsEnabled(false);
            }

            putFeature(feature);
        }
    }

    private FeatureExecutionModel getExecution(FeatureModel feature) {
        if (feature.getExecution() == null) {
            FeatureExecutionModel executionModel = new FeatureExecutionModel();
            executionModel.setFeatureId(feature.getFeatureId());
            executionModel.setAttempts(0);
            executionModel.setExecutions(0);
            executionModel.setErrors(0);
            feature.setExecution(executionModel);
        }

        return feature.getExecution();
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

