package dials.messages;

import dials.filter.FilterData;

public class DataFilterApplicationMessage extends ContextualMessage {

    private FilterData filterData;

    public DataFilterApplicationMessage(FilterData filterData, ContextualMessage message) {
        super(message);
        this.filterData = filterData;
    }

    public FilterData getFilterData() {
        return filterData;
    }
}
