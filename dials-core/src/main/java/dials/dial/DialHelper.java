package dials.dial;

import dials.Dials;
import dials.datastore.CountTuple;
import dials.datastore.DataStore;
import dials.execution.ExecutionContext;
import dials.filter.FilterData;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;

import java.math.BigDecimal;

public class DialHelper {

    public String getDialPattern(FilterData data, ExecutionContext executionContext) {

        FilterDataHelper helper = new FilterDataHelper(data);

        CountTuple tuple = getCountTuple(helper, executionContext);

        if (tuple != null) {
            String dialIncreaseResult = attemptDialIncrease(helper, tuple, executionContext);

            if (dialIncreaseResult.equals("")) {
                return attemptDialDecrease(helper, tuple, executionContext);
            } else {
                return dialIncreaseResult;
            }
        }

        return "";
    }

    private CountTuple getCountTuple(FilterDataHelper helper, ExecutionContext executionContext) {
        DataStore dataStore = Dials.getRegisteredDataStore();

        int frequency;

        try {
            frequency = helper.getData(DialConstants.DIAL_FREQUENCY, Integer.class);
        } catch (FilterDataException e) {
            return null;
        }

        int changeCount;

        try {
            changeCount = helper.getData(DialConstants.DIAL_CHANGE_COUNT, Integer.class);
        } catch (FilterDataException e) {
            changeCount = 1;
        }

        CountTuple tuple = dataStore.getExecutionCountTuple(executionContext.getFeatureName());

        if (frequency * changeCount > tuple.getExecutions()) {
            return null;
        }

        return tuple;
    }

    private String attemptDialIncrease(FilterDataHelper helper, CountTuple tuple, ExecutionContext executionContext) {
        try {
            BigDecimal increaseThreshold = helper.getData(DialConstants.DIAL_INCREASE_THRESHOLD, BigDecimal.class);
            if (tuple.getRateOfSuccess().compareTo(increaseThreshold) >= 0) {
                return helper.getData(DialConstants.DIAL_INCREASE_PATTERN, String.class);
            }
        } catch (FilterDataException e) {
            executionContext.addExecutionStep(e.getMessage());
            executionContext.addExecutionStep("Unable to handle dial increase. Will attempt dial decrease.");
        }
        return "";
    }

    private String attemptDialDecrease(FilterDataHelper helper, CountTuple tuple, ExecutionContext executionContext) {
        try {
            BigDecimal decreaseThreshold = helper.getData(DialConstants.DIAL_DECREASE_THRESHOLD, BigDecimal.class);
            if (tuple.getRateOfSuccess().compareTo(decreaseThreshold) == -1) {
                return helper.getData(DialConstants.DIAL_DECREASE_PATTERN, String.class);
            }
        } catch (FilterDataException e) {
            executionContext.addExecutionStep(e.getMessage());
            executionContext.addExecutionStep("Unable to handle dial decrease.");
        }

        return "";
    }
}
