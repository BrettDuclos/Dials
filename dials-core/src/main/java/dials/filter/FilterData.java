package dials.filter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FilterData {

    private Map<String, Object> dataObjects;

    public FilterData() {
        this.dataObjects = new HashMap<>();
    }

    public Map<String, Object> getDataObjects() {
        return Collections.unmodifiableMap(dataObjects);
    }

    public FilterData addDataObject(String key, Object object) {
        this.dataObjects.put(key.toLowerCase(), object);
        return this;
    }
}
