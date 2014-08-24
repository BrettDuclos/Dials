package dials.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "dials_feature")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeatureModel implements Serializable {

    @Id
    @Column(name = "feature_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureId;

    @Column(name = "feature_name")
    private String featureName;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "killswitch_threshold")
    private Integer killswitchThreshold;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FilterModel> filters;

    @OneToOne(optional = true, mappedBy = "feature", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FeatureExecutionModel execution;

    @Transient
    private Map<String, FilterModel> filterMap;

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Integer getKillswitchThreshold() {
        return killswitchThreshold;
    }

    public void setKillswitchThreshold(Integer killswitchThreshold) {
        this.killswitchThreshold = killswitchThreshold;
    }

    public Set<FilterModel> getFilters() {
        return filters;
    }

    public void setFilters(Set<FilterModel> filters) {
        this.filters = filters;
    }

    public FeatureExecutionModel getExecution() {
        return execution;
    }

    public void setExecution(FeatureExecutionModel execution) {
        this.execution = execution;
    }

    @Transient
    public FilterModel getFilter(String filterName) {
        if (filterMap == null) {
            filterMap = new HashMap<>();
            for (FilterModel filter : getFilters()) {
                filterMap.put(filterName, filter);
            }
        }

        return filterMap.get(filterName);
    }
}
