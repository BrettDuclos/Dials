package dials.filter;

import java.util.HashMap;
import java.util.Map;

public class FeatureFilterDataBean {

    private Map<String, Map<String, Object>> filters;

    public FeatureFilterDataBean() {
        filters = new HashMap<>();
    }

    public Map<String, Map<String, Object>> getFilters() {
        return filters;
    }

    public void addFilterData(String filterName, String dataKey, Object dataValue) {
        if (filters.get(filterName) == null) {
            Map<String, Object> filterData = new HashMap<>();
            filters.put(filterName, filterData);
        }

        if (dataKey != null) {
            filters.get(filterName).put(dataKey.toLowerCase(), dataValue);
        }
    }
}
