package dials.messages;

import dials.filter.FilterData;

public class FilterRetrievalRequestMessage extends ContextualMessage {

    private String featureName;
    private FilterData dynamicData;

    public FilterRetrievalRequestMessage(FeatureStateRequestMessage message) {
        super(message);
        this.featureName = message.getFeatureName();
        this.dynamicData = message.getDynamicData();
    }

    public String getFeatureName() {
        return featureName;
    }

    public FilterData getDynamicData() {
        return dynamicData;
    }
}
