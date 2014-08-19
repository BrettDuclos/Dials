package dials.filter;

import dials.messages.DataFilterApplicationMessage;

/**
 * Dynamic data can be considered the data provided at runtime by the client application.
 * If a given filter is a DynamicDataFilter, it is expected to require a set of runtime data to properly execute.
 * <p/>
 * An example of this is the SeededPercentageFeatureFilter which allows the client to provide an arbitrary object as
 * a seed to the randomness of the filter. When provided, it will give a deterministic answer for a given seed, allowing for consistency.
 */
public interface DynamicDataFilter {

    /**
     * Apply runtime data from the client against the filter to allow for proper execution.
     */
    void applyDynamicData(DataFilterApplicationMessage message);
}
