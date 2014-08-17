package dials.filter.impl;

import dials.filter.*;
import dials.messages.ContextualMessage;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeWindowFeatureFilter extends FeatureFilter implements StaticDataFilter {

    public static final String START_TIME = "StartTime";
    public static final String END_TIME = "EndTime";

    private LocalTime startTime;
    private LocalTime endTime;

    public TimeWindowFeatureFilter(FilterData staticFilterData, ContextualMessage message) {
        setStaticData(staticFilterData, message);
    }

    @Override
    public boolean filter() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");

        if (startTime.toDateTimeToday().isBeforeNow() && endTime.toDateTimeToday().isAfterNow()) {
            return true;
        }

        return false;
    }

    @Override
    public void setStaticData(FilterData data, ContextualMessage message) {
        FilterDataHelper helper = new FilterDataHelper(data);

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
