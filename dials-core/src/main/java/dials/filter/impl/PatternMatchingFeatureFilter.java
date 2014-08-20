package dials.filter.impl;

import dials.filter.*;
import dials.messages.DataFilterApplicationMessage;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternMatchingFeatureFilter extends FeatureFilter implements StaticDataFilter, DynamicDataFilter {

    public static final String PATTERN = "Pattern";

    private Pattern pattern;
    private String matchString;


    @Override
    public boolean filter() {
        if (pattern.matcher(matchString).matches()) {
            return true;
        }
        return false;
    }

    @Override
    public void applyStaticData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());

        try {
            String rawPattern = helper.getData(PATTERN, String.class);

            pattern = Pattern.compile(rawPattern);

            recordSuccessfulDataApply(message, PATTERN);
        } catch (FilterDataException | PatternSyntaxException e) {
            recordUnsuccessfulDataApply(message, PATTERN, true, e.getMessage());
        }
    }

    @Override
    public void applyDynamicData(DataFilterApplicationMessage message) {
        FilterDataHelper helper = new FilterDataHelper(message.getFilterData());

        try {
            matchString = helper.getData(DynamicFilterDataConstants.MATCH_STRING, String.class);
            recordSuccessfulDataApply(message, DynamicFilterDataConstants.MATCH_STRING);
        } catch (FilterDataException e) {
            recordUnsuccessfulDataApply(message, DynamicFilterDataConstants.MATCH_STRING, true, e.getMessage());
        }
    }

}

