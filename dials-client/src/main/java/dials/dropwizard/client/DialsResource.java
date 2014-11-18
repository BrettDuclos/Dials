package dials.dropwizard.client;

import dials.filter.FilterData;

public interface DialsResource {

    boolean getState(String feature);

    boolean getState(String feature, FilterData data);

    void registerError(String feature);
}
