package dials.filter.impl;

import dials.Dials;
import dials.dial.Dial;
import dials.dial.DialHelper;
import dials.dial.Dialable;
import dials.execution.ExecutionContext;
import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

public class TimeWindowFeatureFilter extends FeatureFilter implements StaticDataFilter, Dialable {

    public static final String START_TIME = "StartTime";
    public static final String END_TIME = "EndTime";

    private static final int EXPECTED_PATTERN_LENGTH = 3;

    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public boolean filter() {
        if ((startTime.toDateTimeToday().isBeforeNow()
                || startTime.toDateTimeToday().isEqualNow())
                && endTime.toDateTimeToday().isAfterNow()
                || endTime.toDateTimeToday().isEqualNow()) {
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

        dial(message.getExecutionContext());
    }

    @Override
    public void dial(ExecutionContext executionContext) {
        Dial dial = Dials.getRegisteredDataStore().getFilterDial(executionContext.getFeatureName(), this);
        DialHelper helper = new DialHelper(dial);

        String dialPattern = helper.getDialPattern(executionContext);
        TimeWindowPattern timeToAdd = consumeDialPattern(dialPattern);

        if (timeToAdd != null) {
            executionContext.addExecutionStep("Dial with pattern " + dialPattern + " performed on " + getClass().getSimpleName());

            calculateNewTimes(timeToAdd);

            Dials.getRegisteredDataStore().updateStaticData(dial.getFeatureFilterId(), START_TIME,
                    DateTimeFormat.forPattern("HH:mm:ss").print(startTime));
            Dials.getRegisteredDataStore().updateStaticData(dial.getFeatureFilterId(), END_TIME,
                    DateTimeFormat.forPattern("HH:mm:ss").print(endTime));
            Dials.getRegisteredDataStore().registerDialAttempt(dial.getFeatureFilterId());

            executionContext.addExecutionStep("Dial successfully executed. New start time is " + startTime + " new end time is " + endTime);

            if (startTime.toDateTimeToday().isAfter(endTime.toDateTimeToday())) {
                Dials.getRegisteredDataStore().disableFeature(executionContext.getFeatureName());
                executionContext.addExecutionStep("Start time is now after end time, disabling feature.");
            }
        }
    }

    /**
     * Dial pattern for TimeWindowFeatureFilter is (Integer Unit Direction).
     * <p/>
     * Examples:
     * 1 hour end (Increase Pattern) - Add 1 hour to the end time.
     * 5 minutes start (Increase Pattern) - Subtract 5 minutes from the start time.
     * -2 hours both (Decrease Pattern) - Subtract 2 hours from the end time, add 2 hours to the start time.
     * <p/>
     * <p/>
     * Unit can be one of (hour, hours, minute, minutes)
     * Direction can be one of (start, end, both)
     */
    @Override
    public TimeWindowPattern consumeDialPattern(String pattern) {
        String[] splitPattern = pattern.split(" ");
        if (splitPattern.length == EXPECTED_PATTERN_LENGTH) {
            try {
                return new TimeWindowPattern(Integer.parseInt(splitPattern[0]), splitPattern[1], splitPattern[2]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    private void calculateNewTimes(TimeWindowPattern timeToAdd) {
        String unit = timeToAdd.getUnit();
        String direction = timeToAdd.getDirection();

        int minutesToAdd = 0;
        if (unit.startsWith("hour") || unit.startsWith("Hour")) {
            minutesToAdd = Hours.hours(timeToAdd.getAmount()).toStandardMinutes().getMinutes();
        } else if (unit.startsWith("minute") || unit.startsWith("Minute")) {
            minutesToAdd = timeToAdd.getAmount();
        }

        LocalTime newStartTime = startTime.minusMinutes(minutesToAdd);
        LocalTime newEndTime = endTime.plusMinutes(minutesToAdd);

        if (newStartTime.isAfter(startTime) && minutesToAdd > 0) {
            newStartTime = LocalTime.MIDNIGHT;
        }

        if (newEndTime.isBefore(endTime) && minutesToAdd > 0) {
            newEndTime = LocalTime.MIDNIGHT.minusMillis(1);
        }

        switch (direction) {
            case "start":
                startTime = newStartTime;
                break;
            case "end":
                endTime = newEndTime;
                break;
            case "both":
                startTime = newStartTime;
                endTime = newEndTime;
                break;
        }
    }

    private class TimeWindowPattern {
        private Integer amount;
        private String unit;
        private String direction;

        private TimeWindowPattern(Integer amount, String unit, String direction) {
            this.amount = amount;
            this.unit = unit;
            this.direction = direction;
        }

        public Integer getAmount() {
            return amount;
        }

        public String getUnit() {
            return unit;
        }

        public String getDirection() {
            return direction;
        }
    }
}
