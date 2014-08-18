package dials.filter.impl;

import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import org.joda.time.DateTime;

public class DateRangeFeatureFilter extends FeatureFilter implements StaticDataFilter {

    public static final String START_DATE = "StartDate";
    public static final String END_DATE = "EndDate";

    private DateTime startDate;
    private DateTime endDate;

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
    public void applyStaticData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());

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

