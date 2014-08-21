package dials.filter.impl;

import dials.dial.Dial;
import dials.dial.DialHelper;
import dials.dial.Dialable;
import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class DateRangeFeatureFilter extends FeatureFilter implements StaticDataFilter, Dialable {

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

    @Override
    public void dial(ContextualMessage message) {
        Dial dial = message.getConfiguration().getDataStore().getFilterDial(message.getExecutionContext().getFeatureName(), this);
        DialHelper helper = new DialHelper(dial);

        String dialPattern = helper.getDialPattern(message);
        Integer daysToAdd = consumeDialPattern(dialPattern);

        if (daysToAdd != null) {
            message.getExecutionContext().addExecutionStep("Dial with pattern " + dialPattern
                    + " performed on " + getClass().getSimpleName());

            DateTime newEndDate = endDate.plusDays(daysToAdd);

            message.getConfiguration().getDataStore().updateStaticData(dial.getFeatureFilterId(), END_DATE,
                    DateTimeFormat.forPattern("yyyy-MM-dd").print(newEndDate));
            message.getConfiguration().getDataStore().registerDialAttempt(dial.getFeatureFilterId());

            message.getExecutionContext().addExecutionStep("Dial successfully executed. New end date is " + endDate);

            if (endDate.isBeforeNow()) {
                message.getConfiguration().getDataStore().disableFeature(message.getExecutionContext().getFeatureName());
                message.getExecutionContext().addExecutionStep("End date is now surpassed, disabling feature.");
            }
        }
    }

    /**
     * Dial pattern for DateRangeFeatureFilter is (Integer).
     * <p/>
     * Examples:
     * 1 (Increase Pattern) - Add 1 day to end date.
     * 5 (Increase Pattern) - Add 5 days to end date.
     * -2 (Decrease Pattern) - Subtract 2 days from end date.
     * <p/>
     * Unit is implied as a day.
     */
    @Override
    public Integer consumeDialPattern(String pattern) {
        try {
            return Integer.parseInt(pattern);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
