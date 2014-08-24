package dials.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "dials_feature_filter")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FilterModel implements Serializable {

    @Id
    @Column(name = "feature_filter_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureFilterId;

    @ManyToOne
    @JoinColumn(name = "feature_id")
    private FeatureModel feature;

    @Column(name = "filter_name")
    private String filterName;

    @OneToMany(mappedBy = "filter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FilterStaticDataModel> staticData;

    @OneToOne(optional = true, mappedBy = "filter", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FilterDialModel dial;

    public Integer getFeatureFilterId() {
        return featureFilterId;
    }

    public void setFeatureFilterId(Integer featureFilterId) {
        this.featureFilterId = featureFilterId;
    }

    public FeatureModel getFeature() {
        return feature;
    }

    public void setFeature(FeatureModel feature) {
        this.feature = feature;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public Set<FilterStaticDataModel> getStaticData() {
        return staticData;
    }

    public void setStaticData(Set<FilterStaticDataModel> staticData) {
        this.staticData = staticData;
    }

    public FilterDialModel getDial() {
        return dial;
    }

    public void setDial(FilterDialModel dial) {
        this.dial = dial;
    }

    @Transient
    public void updateStaticData(String key, String value) {
        for (FilterStaticDataModel staticData : getStaticData()) {
            if (staticData.getDataKey().equals(key)) {
                staticData.setDataValue(value);
            }
        }
    }

}
