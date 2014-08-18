package dials.filter;

import dials.messages.DataFilterApplicationMessage;

public interface StaticDataFilter {

    void applyStaticData(DataFilterApplicationMessage message);
}
