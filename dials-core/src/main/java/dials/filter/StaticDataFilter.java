package dials.filter;

import dials.messages.ContextualMessage;

public interface StaticDataFilter {

    void setStaticData(FilterData data, ContextualMessage message);
}
