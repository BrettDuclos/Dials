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
        if (applyRequiredData(message, helper)) {
            dial(message.getExecutionContext());
        }
    }

    private boolean applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        try {
            percentage = helper.getData(PERCENTAGE, Integer.class);
            recordSuccessfulDataApply(message, PERCENTAGE);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, PERCENTAGE, true, e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void dial(ExecutionContext executionContext) {
        Dial dial = Dials.getRegisteredDataStore().getFilterDial(executionContext.getFeatureName(), this);
        DialHelper helper = new DialHelper(dial);

        String dialPattern = helper.getDialPattern(executionContext);
        Integer dialAmount = consumeDialPattern(dialPattern);

        if (dialAmount != null) {
            executionContext.addExecutionStep("Dial with pattern " + dialPattern + " performed on " + getClass().getSimpleName());

            if (dialAmount > MIN_PERCENTAGE && percentage + dialAmount >= MAX_PERCENTAGE) {
                percentage = MAX_PERCENTAGE;
            } else if (dialAmount < MIN_PERCENTAGE && percentage - dialAmount <= MIN_PERCENTAGE) {
                percentage = MIN_PERCENTAGE;
            } else {
                percentage += dialAmount;
            }

            Dials.getRegisteredDataStore().updateStaticData(dial.getFeatureFilterId(), PERCENTAGE, percentage.toString());
            Dials.getRegisteredDataStore().registerDialAttempt(dial.getFeatureFilterId());

            executionContext.addExecutionStep("Dial successfully executed. New percentage is " + percentage);

            if (percentage == MIN_PERCENTAGE) {
                Dials.getRegisteredDataStore().disableFeature(executionContext.getFeatureName());
                executionContext.addExecutionStep("Percentage has reached 0, disabling feature.");
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
