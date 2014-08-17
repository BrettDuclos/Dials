package dials.filter.impl;

import dials.filter.*;
import dials.messages.ContextualMessage;

import java.util.Random;

public class SeededPercentageFeatureFilter extends PercentageFeatureFilter implements DynamicDataFilter {

    private Long seed;

    public SeededPercentageFeatureFilter(FilterData staticFilterData, FilterData dynamicFilterData, ContextualMessage message) {
        super(staticFilterData, message);
        setDynamicData(dynamicFilterData, message);
    }

    @Override
    protected Random getRandom() {
        return new Random(seed);
    }

    @Override
    public void setDynamicData(FilterData data, ContextualMessage message) {
        FilterDataHelper helper = new FilterDataHelper(data);
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
