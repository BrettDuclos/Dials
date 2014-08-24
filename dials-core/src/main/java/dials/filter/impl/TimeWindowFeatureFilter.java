package dials.filter.impl;

import dials.dial.DialHelper;
import dials.dial.Dialable;
import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import dials.model.FeatureModel;
import dials.model.FilterModel;
import org.joda.time.Hours;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

public class TimeWindowFeatureFilter extends FeatureFilter implements StaticDataFilter, Dialable {

    public static final String START_TIME = "StartTime";
    public static final String END_TIME = "EndTime";

    private static final int EXPECTED_PATTERN_LENGTH = 3;
    private static final int MINUTES_IN_DAY = 1440;

    private LocalTime startTime;
    private Integer timeWindowInMinutes;
    private boolean crossesMidnight;

    @Override
    public boolean filter() {
        LocalTime now = LocalTime.now();

        if (timeWindowInMinutes >= MINUTES_IN_DAY) {
            return true;
        } else if (crossesMidnight && (now.isAfter(startTime) || now.isBefore(startTime.plusMinutes(timeWindowInMinutes)))) {
            return true;
        } else if (!crossesMidnight && now.isAfter(startTime) && now.isBefore(startTime.plusMinutes(timeWindowInMinutes))) {
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

        LocalTime endTime = startTime;
        try {
            endTime = helper.getData(END_TIME, LocalTime.class);
            recordSuccessfulDataApply(message, END_TIME);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, END_TIME, false, e.getMessage());
            success = false;
        }

        if (!success) {
            abandon(message);
        } else if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            Minutes minutes = Minutes.minutesBetween(endTime, startTime);
            timeWindowInMinutes = MINUTES_IN_DAY - minutes.getMinutes();
            crossesMidnight = true;
        } else {
            Minutes minutes = Minutes.minutesBetween(startTime, endTime);
            timeWindowInMinutes = minutes.getMinutes();
        }
    }

    @Override
    public void dial(ContextualMessage message, String filterName) {
        FeatureModel feature = message.getFeature();
        FilterModel filter = feature.getFilter(filterName);

        DialHelper helper = new DialHelper(filter.getDial());

        String dialPattern = helper.getDialPattern(message);
        TimeWindowPattern timeToAdd = consumeDialPattern(dialPattern);

        if (timeToAdd != null) {
            message.getExecutionContext().addExecutionStep("Dial with pattern " + dialPattern + " performed on "
                    + getClass().getSimpleName());

            calculateNewTimes(timeToAdd);

            message.performDialAdjustment(feature.getFeatureName(), filterName, START_TIME,
                    DateTimeFormat.forPattern("HH:mm:ss").print(startTime));
            message.performDialAdjustment(feature.getFeatureName(), filterName, END_TIME,
                    DateTimeFormat.forPattern("HH:mm:ss").print(startTime.plusMinutes(timeWindowInMinutes)));

            message.getExecutionContext().addExecutionStep("Dial successfully executed. New start time is "
                    + startTime + " new end time is " + startTime.plusMinutes(timeWindowInMinutes));

            if (startTime.toDateTimeToday().isAfter(startTime.plusMinutes(timeWindowInMinutes).toDateTimeToday())) {
                message.disableFeature(feature.getFeatureName());
                message.getExecutionContext().addExecutionStep("Start time is now after end time, disabling feature.");
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

    protected void calculateNewTimes(TimeWindowPattern timeToAdd) {
        String unit = timeToAdd.getUnit();
        String direction = timeToAdd.getDirection();

        int minutesToAdd = 0;
        if (unit.toLowerCase().startsWith("hour")) {
            minutesToAdd = Hours.hours(timeToAdd.getAmount()).toStandardMinutes().getMinutes();
        } else if (unit.toLowerCase().startsWith("minute")) {
            minutesToAdd = timeToAdd.getAmount();
        }

        LocalTime newStartTime;

        if (direction.equals("start")) {
            newStartTime = startTime.minusMinutes(minutesToAdd);
            timeWindowInMinutes += minutesToAdd;
        } else if (direction.equals("end")) {
            newStartTime = startTime;
            timeWindowInMinutes += minutesToAdd;
        } else {
            newStartTime = startTime.minusMinutes(minutesToAdd);
            timeWindowInMinutes += minutesToAdd * 2;
        }

        if (newStartTime.isAfter(newStartTime.plusMinutes(timeWindowInMinutes)) || timeWindowInMinutes >= MINUTES_IN_DAY) {
            crossesMidnight = true;
        } else {
            crossesMidnight = false;
        }

        if (timeWindowInMinutes <= MINUTES_IN_DAY) {
            switch (direction) {
                case "start":
                case "both":
                    startTime = newStartTime;
                    break;
                case "end":
                    break;
            }
        } else {
            timeWindowInMinutes = MINUTES_IN_DAY;
        }
    }

    protected LocalTime getStartTime() {
        return startTime;
    }

    protected LocalTime getEndTime() {
        return startTime.plusMinutes(timeWindowInMinutes);
    }

    protected boolean getCrossesMidnight() {
        return crossesMidnight;
    }

    protected static class TimeWindowPattern {
        private Integer amount;
        private String unit;
        private String direction;

        public TimeWindowPattern(Integer amount, String unit, String direction) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TimeWindowPattern that = (TimeWindowPattern) o;

            if (amount != null ? !amount.equals(that.amount) : that.amount != null) {
                return false;
            }

            if (direction != null ? !direction.equals(that.direction) : that.direction != null) {
                return false;
            }

            if (unit != null ? !unit.equals(that.unit) : that.unit != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = amount != null ? amount.hashCode() : 0;
            result = 31 * result + (unit != null ? unit.hashCode() : 0);
            result = 31 * result + (direction != null ? direction.hashCode() : 0);
            return result;
        }
    }

}
