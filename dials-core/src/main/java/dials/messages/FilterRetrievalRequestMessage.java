package dials.messages;

import dials.filter.FilterData;

public class FilterRetrievalRequestMessage extends ContextualMessage {

    private FilterData dynamicData;

    public FilterRetrievalRequestMessage(FeatureStateRequestMessage message) {
        super(message);
        this.dynamicData = message.getDynamicData();
    }

    public FilterData getDynamicData() {
        return dynamicData;
    }
}
