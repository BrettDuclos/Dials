package dials.filter.impl;

import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import org.joda.time.LocalTime;

public class TimeWindowFeatureFilter extends FeatureFilter implements StaticDataFilter {

    public static final String START_TIME = "StartTime";
    public static final String END_TIME = "EndTime";

    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public boolean filter() {
        if (startTime.toDateTimeToday().isBeforeNow() && endTime.toDateTimeToday().isAfterNow()) {
            return true;
        }

        return false;
    }

    @Override
    public void applyStaticData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());

        applyRequiredData(message, helper);
    }

    private void applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        boolean success = true;

        try {
            startTime = helper.getData(START_TIME, LocalTime.class);
            recordSuccessfulDataApply(message, START_TIME);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, START_TIME, false, e.getMessage());
            success = false;
        }

        try {
            endTime = helper.getData(END_TIME, LocalTime.class);
            recordSuccessfulDataApply(message, END_TIME);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, END_TIME, false, e.getMessage());
            success = false;
        }

        if (!success) {
            abandon(message);
        }
    }
}
