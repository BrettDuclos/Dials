package dials.dial;

import dials.datastore.CountTuple;
import dials.messages.ContextualMessage;
import dials.model.FilterDialModel;

import java.math.BigDecimal;

public class DialHelper {

    public static String ATTEMPTED = "attempted";

    private FilterDialModel dial;

    public DialHelper(FilterDialModel dial) {
        this.dial = dial;
    }

    public String getDialPattern(ContextualMessage message) {
        if (dial != null) {
            CountTuple tuple = getCountTuple(dial, message);

            if (tuple != null) {
                String dialIncreaseResult = determineDialIncreaseEligibility(dial, tuple, message);

                if (!dialIncreaseResult.equals("")) {
                    return dialIncreaseResult;
                }

                String dialDecreaseResult = determineDialDecreaseEligibility(dial, tuple, message);

                if (!dialDecreaseResult.equals("")) {
                    return dialDecreaseResult;
                }

                return ATTEMPTED;
            }
        }

        return "";
    }

    private CountTuple getCountTuple(FilterDialModel dial, ContextualMessage message) {
        CountTuple tuple = new CountTuple(message.getFeature().getExecution().getExecutions(),
                message.getFeature().getExecution().getErrors());

        if (dial.getFrequency() + (dial.getFrequency() * dial.getAttempts()) > tuple.getExecutions()) {
            return null;
        }

        return tuple;
    }

    private String determineDialIncreaseEligibility(FilterDialModel dial, CountTuple tuple, ContextualMessage message) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getIncreaseThreshold())) >= 0) {
            return dial.getIncreasePattern();
        }

        message.getExecutionContext().addExecutionStep("Filter not eligible for Dial increase.");

        return "";
    }

    private String determineDialDecreaseEligibility(FilterDialModel dial, CountTuple tuple, ContextualMessage message) {
        if (tuple.getRateOfSuccess().compareTo(new BigDecimal(dial.getDecreaseThreshold())) < 0) {
            return dial.getDecreasePattern();
        }

        message.getExecutionContext().addExecutionStep("Filter not eligible for Dial decrease.");

        return "";
    }
}
