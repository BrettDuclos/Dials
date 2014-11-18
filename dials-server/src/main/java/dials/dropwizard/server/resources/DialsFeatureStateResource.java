package dials.dropwizard.server.resources;

import dials.Dials;
import dials.dropwizard.api.FeatureStateRequest;
import dials.dropwizard.api.FeatureStateResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dials")
public class DialsFeatureStateResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{featureName}")
    public FeatureStateResponse getState(@PathParam("featureName") String featureName) {
        return new FeatureStateResponse(Dials.getState(featureName));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{featureName}")
    public FeatureStateResponse getState(@PathParam("featureName") String featureName, FeatureStateRequest request) {
        return new FeatureStateResponse(Dials.getState(featureName, request.getData()));
    }

    @POST
    @Path("/{featureName}/error")
    public Response registerError(@PathParam("featureName") String featureName) {
        Dials.sendError(featureName);
        return Response.ok().build();
    }
}

