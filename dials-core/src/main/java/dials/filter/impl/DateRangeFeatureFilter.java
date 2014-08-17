package dials.filter.impl;

import dials.filter.*;
import dials.messages.ContextualMessage;
import org.joda.time.DateTime;

public class DateRangeFeatureFilter extends FeatureFilter implements StaticDataFilter {

    public static final String START_DATE = "StartDate";
    public static final String END_DATE = "EndDate";

    private DateTime startDate;
    private DateTime endDate;

    public DateRangeFeatureFilter(FilterData data, ContextualMessage message) {
        setStaticData(data, message);
    }

    @Override
    public boolean filter() {
        if (startDate.isAfterNow()) {
            return false;
        } else if (endDate != null && endDate.isBeforeNow()) {
            return false;
        }

        return true;
    }

    @Override
    public void setStaticData(FilterData data, ContextualMessage message) {
        FilterDataHelper helper = new FilterDataHelper(data);

        applyRequiredData(message, helper);
        applyOptionalData(message, helper);
    }

    private void applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        try {
            startDate = helper.getData(START_DATE, DateTime.class);
            recordSuccessfulDataApply(message, START_DATE);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, START_DATE, true, e.getMessage());
        }
    }

    private void applyOptionalData(ContextualMessage message, FilterDataHelper helper) {
        try {
            endDate = helper.getData(END_DATE, DateTime.class);
            recordSuccessfulDataApply(message, END_DATE);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, END_DATE, false, e.getMessage());
        }
    }
}

