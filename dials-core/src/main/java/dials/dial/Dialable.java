package dials.dial;

import dials.messages.ContextualMessage;

public interface Dialable {

    /**
     * Attempt to dial a filter. The dialing of a feature can include an increase(expansion), decrease(contraction), or both.
     * The dialing of a filter is dependent on how it can logically increase or decrease. Not all Filters may be Dialable.
     */
    void dial(ContextualMessage message, String filterName);

    /**
     * Based on the pattern retrieved for a given dial, consume it in a meaningful manner for the given filter.
     * <p/>
     * Patterns are a relatively free form concept to allow for the differences in filter increase or decrease.
     * For instance, a Date Range filter may have a pattern of 'add 1 day' or 'remove 1 day' which when consumed by the filter
     * would result in the addition or removal of a day from the end of the date range.
     */
    Object consumeDialPattern(String pattern);
}
