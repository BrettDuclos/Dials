package dials.dial;

import dials.datastore.CountTuple;
import dials.datastore.DataStore;
import dials.messages.ContextualMessage;

import java.math.BigDecimal;

public class DialHelper {

    private Dial dial;

    public DialHelper(Dial dial) {
        this.dial = dial;
    }

    public String getDialPattern(ContextualMessage message) {
        if (dial != null) {
            CountTuple tuple = getCountTuple(dial, message);

            if (tuple != null) {
                String dialIncreaseResult = determineDialIncreaseEligibility(dial, tuple, message);

                if (dialIncreaseResult.equals("")) {
                    return determineDialDecreaseEligibility(dial, tuple, message);
                } else {
                    return dialIncreaseResult;
                }
            }
        }

        return "";
    }

    private CountTuple getCountTuple(Dial dial, ContextualMessage message) {
        DataStore dataStore = message.getConfiguration().getDataStore();

        CountTuple tuple = dataStore.getExecutionCountTuple(message.getExecutionContext().getFeatureName());

        if (dial.getFrequency() + (dial.getFrequency() * dial.getAttempts()) > tuple.getExecutions()) {
            return null;
        }

        return tuple;
    }

    private String determineDialIncreaseEligibility(Dial dial, CountTuple tuple, ContextualMessage message) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getIncreaseThreshold())) >= 0) {
            return dial.getIncreasePattern();
        }

        message.getExecutionContext().addExecutionStep("Filter not eligible for Dial increase.");

        return "";
    }

    private String determineDialDecreaseEligibility(Dial dial, CountTuple tuple, ContextualMessage message) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getDecreaseThreshold())) < 0) {
            return dial.getDecreasePattern();
        }

        message.getExecutionContext().addExecutionStep("Filter not eligible for Dial decrease.");

        return "";
    }
}
