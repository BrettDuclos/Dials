package dials.messages;

public class RegisterErrorMessage {

    private String featureName;

    public RegisterErrorMessage(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }
}
