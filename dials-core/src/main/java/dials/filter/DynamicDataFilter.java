package dials.filter;

import dials.messages.DataFilterApplicationMessage;

public interface DynamicDataFilter {

    void applyDynamicData(DataFilterApplicationMessage message);
}
