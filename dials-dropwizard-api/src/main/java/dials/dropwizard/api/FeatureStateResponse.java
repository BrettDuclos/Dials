package dials.dropwizard.api;

public class FeatureStateResponse {

    private boolean state;

    public FeatureStateResponse() {

    }

    public FeatureStateResponse(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
