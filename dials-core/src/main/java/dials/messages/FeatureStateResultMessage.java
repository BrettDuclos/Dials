package dials.messages;

public class FeatureStateResultMessage {

    private boolean state;

    public FeatureStateResultMessage(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
