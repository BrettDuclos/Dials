package dials.filter;

import dials.messages.ContextualMessage;

public interface DynamicDataFilter {

    void setDynamicData(FilterData data, ContextualMessage message);
}
