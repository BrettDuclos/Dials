package dials.filter;

import dials.messages.DataFilterApplicationMessage;

/**
 * Static data can be considered the data provided within a given data store for the execution of a filter.
 * Most (if not all) filters will require some set of static data to properly execute. It would be plausible to
 * represent your static data as data directly within the filter if it is likely to never change and requires no customization.
 * If a given filter is a StaticDataFilter, it is expected to require a set of control data to properly execute.
 * <p/>
 * An example of this is the PercentageFeatureFilter which requires a static percentage that is the percentage of time
 * that the filter will execute.
 */
public interface StaticDataFilter {

    /**
     * Apply control data from the datastore against the filter to allow for proper execution.
     */
    void applyStaticData(DataFilterApplicationMessage message);
}
