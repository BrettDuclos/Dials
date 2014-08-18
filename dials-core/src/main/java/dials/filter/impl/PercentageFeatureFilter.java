package dials.filter.impl;

import dials.Dials;
import dials.dial.DialConstants;
import dials.dial.DialHelper;
import dials.dial.Dialable;
import dials.execution.ExecutionContext;
import dials.filter.*;
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
            dial(message.getFilterData(), message.getExecutionContext());
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
    public void dial(FilterData data, ExecutionContext executionContext) {
        DialHelper helper = new DialHelper();
        String dialPattern = helper.getDialPattern(data, executionContext);

        if (dialPattern.equals("")) {
            return;
        }

        int dialAmount;

        try {
            dialAmount = Integer.parseInt(dialPattern);
        } catch (NumberFormatException e) {
            return;
        }

        executionContext.addExecutionStep("Dial performed on " + getClass().getName() + " " + dialPattern);

        if (dialAmount > MIN_PERCENTAGE && percentage + dialAmount >= MAX_PERCENTAGE) {
            percentage = MAX_PERCENTAGE;
        } else if (dialAmount < MIN_PERCENTAGE && percentage - dialAmount <= MIN_PERCENTAGE) {
            percentage = MIN_PERCENTAGE;
        } else {
            percentage += dialAmount;
        }

        Dials.getRegisteredDataStore().updateStaticData(executionContext.getFeatureName(), PERCENTAGE, percentage.toString());
        Dials.getRegisteredDataStore().updateStaticData(executionContext.getFeatureName(), DialConstants.DIAL_CHANGE_COUNT,
                String.valueOf(Integer.parseInt((String) data.getDataObjects().get(DialConstants.DIAL_CHANGE_COUNT)) + 1));
    }
}
