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

import java.util.Random;

public class PercentageFeatureFilter extends FeatureFilter implements StaticDataFilter, Dialable {
    public static final String PERCENTAGE = "Percentage";
    private static final int MAX_PERCENTAGE = 100;
    private static final int MIN_PERCENTAGE = 0;

    private Integer percentage;

    @Override
    public boolean filter() {
        Random random = getRandom();
        int randomValue = random.nextInt(MAX_PERCENTAGE);

        if (randomValue < percentage) {
            return true;
        }

        return false;
    }

    protected Random getRandom() {
        return new Random();
    }

    @Override
    public void applyStaticData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());
        applyRequiredData(message, helper);
    }

    private void applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        try {
            percentage = helper.getData(PERCENTAGE, Integer.class);

            if (percentage > MAX_PERCENTAGE) {
                percentage = MAX_PERCENTAGE;
            } else if (percentage < MIN_PERCENTAGE) {
                percentage = MIN_PERCENTAGE;
            }

            recordSuccessfulDataApply(message, PERCENTAGE);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, PERCENTAGE, true, e.getMessage());
        }
    }

    @Override
    public void dial(ContextualMessage message, String filterName) {
        FeatureModel feature = message.getFeature();
        FilterModel filter = feature.getFilter(filterName);

        DialHelper helper = new DialHelper(filter.getDial());

        String dialPattern = helper.getDialPattern(message);

        if (dialPattern.equals(DialHelper.ATTEMPTED)) {
            message.performDialAdjustment(feature.getFeatureName(), filterName, PERCENTAGE, percentage.toString());
            return;
        }

        Integer dialAmount = consumeDialPattern(dialPattern);

        if (dialAmount != null) {
            message.getExecutionContext().addExecutionStep("Dial with pattern "
                    + dialPattern + " performed on " + getClass().getSimpleName());

            if (dialAmount > MIN_PERCENTAGE && percentage + dialAmount >= MAX_PERCENTAGE) {
                percentage = MAX_PERCENTAGE;
            } else if (dialAmount < MIN_PERCENTAGE && percentage - dialAmount <= MIN_PERCENTAGE) {
                percentage = MIN_PERCENTAGE;
            } else {
                percentage += dialAmount;
            }

            message.performDialAdjustment(feature.getFeatureName(), filterName, PERCENTAGE, percentage.toString());
            message.getExecutionContext().addExecutionStep("Dial successfully executed. New percentage is " + percentage);

            if (percentage == MIN_PERCENTAGE) {
                message.disableFeature(feature.getFeatureName());
                message.getExecutionContext().addExecutionStep("Percentage has reached 0, disabling feature.");
            }
        }
    }

    /**
     * Dial pattern for PercentageFeatureFilter is (Integer).
     * <p/>
     * Examples:
     * 1 (Increase Pattern) - Increase percentage by 1
     * 5 (Increase Pattern) - Increase percentage by 5
     * -2 (Decrease Pattern) - Decrease percentage by 2
     * <p/>
     * Unit is implied as a percentage.
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
