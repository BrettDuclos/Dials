package dials.messages;

import dials.filter.FilterData;

public class DynamicDataFilterApplicationMessage extends DataFilterApplicationMessage {

    public DynamicDataFilterApplicationMessage(FilterData filterData, ContextualMessage message) {
        super(filterData, message);
    }
}
