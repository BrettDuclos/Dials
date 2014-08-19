package dials.dropwizard.api;

import dials.filter.FilterData;

public class FeatureStateRequest {

    private FilterData data;

    public FeatureStateRequest() {
    }

    public FeatureStateRequest(FilterData data) {
        this.data = data;
    }

    public FilterData getData() {
        return data;
    }

    public void setData(FilterData data) {
        this.data = data;
    }
}
