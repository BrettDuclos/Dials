package dials.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dials_feature_execution")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureExecutionModel implements Serializable {

    @Id
    @Column(name = "feature_execution_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureExecutionId;

    @OneToOne
    @JoinColumn(name = "feature_id")
    private FeatureModel feature;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "executions")
    private Integer executions;

    @Column(name = "errors")
    private Integer errors;

    public Integer getFeatureExecutionId() {
        return featureExecutionId;
    }

    public void setFeatureExecutionId(Integer featureExecutionId) {
        this.featureExecutionId = featureExecutionId;
    }

    public FeatureModel getFeature() {
        return feature;
    }

    public void setFeature(FeatureModel feature) {
        this.feature = feature;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getExecutions() {
        return executions;
    }

    public void setExecutions(Integer executions) {
        this.executions = executions;
    }

    public Integer getErrors() {
        return errors;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    @Transient
    public synchronized void registerAttempt(boolean executed) {
        attempts++;

        if (executed) {
            executions++;
        }
    }

    @Transient
    public synchronized void registerError() {
        errors++;
    }
}
