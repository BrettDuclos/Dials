package dials.dial;

public class Dial {

    private Integer featureFilterId;
    private Integer frequency;
    private Integer attempts;
    private Integer increaseThreshold;
    private String increasePattern;
    private Integer decreaseThreshold;
    private String decreasePattern;

    public Integer getFeatureFilterId() {
        return featureFilterId;
    }

    public void setFeatureFilterId(Integer featureFilterId) {
        this.featureFilterId = featureFilterId;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getIncreaseThreshold() {
        return increaseThreshold;
    }

    public void setIncreaseThreshold(Integer increaseThreshold) {
        this.increaseThreshold = increaseThreshold;
    }

    public String getIncreasePattern() {
        return increasePattern;
    }

    public void setIncreasePattern(String increasePattern) {
        this.increasePattern = increasePattern;
    }

    public Integer getDecreaseThreshold() {
        return decreaseThreshold;
    }

    public void setDecreaseThreshold(Integer decreaseThreshold) {
        this.decreaseThreshold = decreaseThreshold;
    }

    public String getDecreasePattern() {
        return decreasePattern;
    }

    public void setDecreasePattern(String decreasePattern) {
        this.decreasePattern = decreasePattern;
    }
}
