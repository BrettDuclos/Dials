package dials.filter.impl;

import dials.filter.FeatureFilter;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.filter.StaticDataFilter;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Arrays;
import java.util.List;

public class DayOfWeekFeatureFilter extends FeatureFilter implements StaticDataFilter {

    public static final String DAYS_OF_WEEK = "DaysOfWeek";

    private String[] daysOfWeek;

    @Override
    public boolean filter() {
        int currentDayOfWeek = new DateTime().getDayOfWeek();

        for (String dayName : daysOfWeek) {
            if (DayOfWeek.getKeyForName(dayName) == currentDayOfWeek) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void applyStaticData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());
        applyRequiredData(message, helper);
    }

    private void applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        try {
            daysOfWeek = helper.getData(DAYS_OF_WEEK, String.class).split(",");

            if (!validateDayNames(daysOfWeek)) {
                throw new FilterDataException("Invalid day provided for " + getClass().getSimpleName());
            }

            recordSuccessfulDataApply(message, DAYS_OF_WEEK);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, DAYS_OF_WEEK, true, e.getMessage());
        }
    }

    private boolean validateDayNames(String[] days) {
        for (String day : days) {
            if (DayOfWeek.getKeyForName(day) == -1) {
                return false;
            }
        }

        return true;
    }

    private enum DayOfWeek {
        MONDAY(DateTimeConstants.MONDAY, "mon", "monday"), TUESDAY(DateTimeConstants.TUESDAY, "tue", "tuesday"),
        WEDNESDAY(DateTimeConstants.WEDNESDAY, "wed", "wednesday"), THURSDAY(DateTimeConstants.THURSDAY, "thu", "thursday"),
        FRIDAY(DateTimeConstants.FRIDAY, "fri", "friday"), SATURDAY(DateTimeConstants.SATURDAY, "sat", "saturday"),
        SUNDAY(DateTimeConstants.SUNDAY, "sun", "sunday");

        private List<String> names;
        private int key;

        private DayOfWeek(int key, String... names) {
            this.key = key;
            this.names = Arrays.asList(names);
        }

        public static int getKeyForName(String name) {
            for (DayOfWeek day : DayOfWeek.values()) {
                if (day.names.contains(name)) {
                    return day.key;
                }
            }

            return -1;
        }
    }
}
