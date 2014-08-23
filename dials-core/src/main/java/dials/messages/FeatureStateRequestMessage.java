package dials.messages;

import dials.filter.FilterData;

public class FeatureStateRequestMessage extends ContextualMessage {

    private FilterData dynamicData;

    public FeatureStateRequestMessage(ContextualMessage message) {
        this(null, message);
    }

    public FeatureStateRequestMessage(FilterData dynamicData, ContextualMessage message) {
        super(message);
        this.dynamicData = dynamicData;
    }

    public FilterData getDynamicData() {
        return dynamicData;
    }
}
