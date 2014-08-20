package dials.dial;

import dials.Dials;
import dials.datastore.CountTuple;
import dials.datastore.DataStore;
import dials.execution.ExecutionContext;

import java.math.BigDecimal;

public class DialHelper {

    private Dial dial;

    public DialHelper(Dial dial) {
        this.dial = dial;
    }

    public String getDialPattern(ExecutionContext executionContext) {
        if (dial != null) {
            CountTuple tuple = getCountTuple(dial, executionContext);

            if (tuple != null) {
                String dialIncreaseResult = determineDialIncreaseEligibility(dial, tuple, executionContext);

                if (dialIncreaseResult.equals("")) {
                    return determineDialDecreaseEligibility(dial, tuple, executionContext);
                } else {
                    return dialIncreaseResult;
                }
            }
        }

        return "";
    }

    private CountTuple getCountTuple(Dial dial, ExecutionContext executionContext) {
        DataStore dataStore = Dials.getRegisteredDataStore();

        CountTuple tuple = dataStore.getExecutionCountTuple(executionContext.getFeatureName());

        if (dial.getFrequency() + (dial.getFrequency() * dial.getAttempts()) > tuple.getExecutions()) {
            return null;
        }

        return tuple;
    }

    private String determineDialIncreaseEligibility(Dial dial, CountTuple tuple, ExecutionContext executionContext) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getIncreaseThreshold())) >= 0) {
            return dial.getIncreasePattern();
        }

        executionContext.addExecutionStep("Filter not eligible for Dial increase.");

        return "";
    }

    private String determineDialDecreaseEligibility(Dial dial, CountTuple tuple, ExecutionContext executionContext) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getDecreaseThreshold())) < 0) {
            return dial.getDecreasePattern();
        }

        executionContext.addExecutionStep("Filter not eligible for Dial decrease.");

        return "";
    }
}
