package dials.messages;

import dials.DialsSystemConfiguration;

public class RegisterErrorMessage {

    private String featureName;
    private DialsSystemConfiguration configuration;

    public RegisterErrorMessage(String featureName, DialsSystemConfiguration configuration) {
        this.featureName = featureName;
        this.configuration = configuration;
    }

    public String getFeatureName() {
        return featureName;
    }

    public DialsSystemConfiguration getConfiguration() {
        return configuration;
    }
}
