package dials.messages;

import dials.filter.FilterData;

public class StaticDataFilterApplicationMessage extends DataFilterApplicationMessage {

    public StaticDataFilterApplicationMessage(FilterData filterData, ContextualMessage message) {
        super(filterData, message);
    }
}
