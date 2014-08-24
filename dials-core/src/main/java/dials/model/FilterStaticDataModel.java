package dials.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "dials_feature_filter_static_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FilterStaticDataModel implements Serializable {

    @Id
    @Column(name = "feature_filter_static_data_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer featureFilterStaticDataId;

    @ManyToOne
    @JoinColumn(name = "feature_filter_id")
    private FilterModel filter;

    @Column(name = "data_key")
    private String dataKey;

    @Column(name = "data_value")
    private String dataValue;

    public Integer getFeatureFilterStaticDataId() {
        return featureFilterStaticDataId;
    }

    public void setFeatureFilterStaticDataId(Integer featureFilterStaticDataId) {
        this.featureFilterStaticDataId = featureFilterStaticDataId;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
