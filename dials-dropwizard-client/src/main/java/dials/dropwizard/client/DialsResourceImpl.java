package dials.dropwizard.client;

import com.sun.jersey.api.client.WebResource;
import dials.dropwizard.api.FeatureStateRequest;
import dials.dropwizard.api.FeatureStateResponse;
import dials.filter.FilterData;

import javax.ws.rs.core.MediaType;

public class DialsResourceImpl implements DialsResource {

    private final WebResource dialsResource;

    public DialsResourceImpl(WebResource appResource) {
        this.dialsResource = appResource.path("dials");
    }

    @Override
    public boolean getState(String feature) {
        return dialsResource.path(feature).accept(MediaType.APPLICATION_JSON_TYPE).get(FeatureStateResponse.class).getState();
    }

    @Override
    public boolean getState(String feature, FilterData data) {
        return dialsResource.path(feature).accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE)
                .post(FeatureStateResponse.class, new FeatureStateRequest(data)).getState();
    }

    @Override
    public void registerError(String feature) {
        dialsResource.path(feature).path("error").post();
    }
}
