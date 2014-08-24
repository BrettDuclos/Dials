package dials.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dials_feature_filter_dial")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FilterDialModel implements Serializable {

    @Id
    @Column(name = "feature_filter_dial_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureFilterDialId;

    @OneToOne
    @JoinColumn(name = "feature_filter_id")
    private FilterModel filter;

    @Column(name = "frequency")
    private Integer frequency;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "increase_threshold")
    private Integer increaseThreshold;

    @Column(name = "increase_pattern")
    private String increasePattern;

    @Column(name = "decrease_threshold")
    private Integer decreaseThreshold;

    @Column(name = "decrease_pattern")
    private String decreasePattern;

    public Integer getFeatureFilterDialId() {
        return featureFilterDialId;
    }

    public void setFeatureFilterDialId(Integer featureFilterDialId) {
        this.featureFilterDialId = featureFilterDialId;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
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

    @Transient
    public void registerAttempt() {
        attempts++;
    }
}
