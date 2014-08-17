package dials.messages;

import dials.filter.FilterData;

public class FeatureStateRequestMessage extends ContextualMessage {

    private String featureName;
    private FilterData dynamicData;

    public FeatureStateRequestMessage(String featureName, ContextualMessage message) {
        this(featureName, null, message);
    }

    public FeatureStateRequestMessage(String featureName, FilterData dynamicData, ContextualMessage message) {
        super(message);
        this.featureName = featureName;
        this.dynamicData = dynamicData;
    }

    public String getFeatureName() {
        return featureName;
    }

    public FilterData getDynamicData() {
        return dynamicData;
    }
}
