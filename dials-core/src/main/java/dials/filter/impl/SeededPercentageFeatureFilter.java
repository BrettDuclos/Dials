package dials.filter.impl;

import dials.filter.DynamicDataFilter;
import dials.filter.DynamicFilterDataConstants;
import dials.filter.FilterDataException;
import dials.filter.FilterDataHelper;
import dials.messages.ContextualMessage;
import dials.messages.DataFilterApplicationMessage;

import java.util.Random;

public class SeededPercentageFeatureFilter extends PercentageFeatureFilter implements DynamicDataFilter {

    private Long seed;

    @Override
    protected Random getRandom() {
        return new Random(seed);
    }

    @Override
    public void applyDynamicData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());
        applyRequiredData(message, helper);
    }

    private void applyRequiredData(ContextualMessage message, FilterDataHelper helper) {
        try {
            seed = generateSeed(helper.getData(DynamicFilterDataConstants.SEED, Object.class),
                    message.getExecutionContext().getFeatureName());
            recordSuccessfulDataApply(message, DynamicFilterDataConstants.SEED);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, DynamicFilterDataConstants.SEED, true, e.getMessage());
        }
    }

    private Long generateSeed(Object dynamicSeed, String featureName) {
        return ((long) dynamicSeed.hashCode()) + ((long) featureName.hashCode());
    }
}
