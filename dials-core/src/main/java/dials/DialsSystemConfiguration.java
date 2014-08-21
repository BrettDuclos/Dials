package dials;

import dials.datastore.DataStore;
import dials.execution.ExecutionContextRecorder;
import dials.execution.NoopExecutionContextRecorder;
import dials.filter.FeatureFilter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DialsSystemConfiguration {

    private Logger logger = LoggerFactory.getLogger(DialsSystemConfiguration.class);

    private DataStore dataStore;
    private ExecutionContextRecorder executionContextRecorder;
    private boolean failFastEnabled = true;
    private Map<String, Class<? extends FeatureFilter>> availableFeatureFilters;

    public DialsSystemConfiguration() {
        availableFeatureFilters = new HashMap<>();
    }

    public DialsSystemConfiguration withDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
        return this;
    }

    public DialsSystemConfiguration withExecutionContextRecorder(ExecutionContextRecorder executionContextRecorder) {
        this.executionContextRecorder = executionContextRecorder;
        return this;
    }

    public DialsSystemConfiguration withFailFastEnabled(boolean failFastEnabled) {
        this.failFastEnabled = failFastEnabled;
        return this;
    }

    public void initializeSystem() {
        if (dataStore == null) {
            logger.error("A DataStore is required to initialize the Dials system.");
            return;
        }

        if (executionContextRecorder == null) {
            executionContextRecorder = new NoopExecutionContextRecorder();
        }

        Reflections reflections = new Reflections("dials");
        Set<Class<? extends FeatureFilter>> filters = reflections.getSubTypesOf(FeatureFilter.class);

        for (Class<? extends FeatureFilter> filter : filters) {
            String key = filter.getSimpleName().replace("FeatureFilter", "");
            availableFeatureFilters.put(key.toLowerCase(), filter);
            logger.info("Detected Feature Filter - " + key);
        }

        Dials.init(this);
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public ExecutionContextRecorder getExecutionContextRecorder() {
        return executionContextRecorder;
    }

    public boolean isFailFastEnabled() {
        return failFastEnabled;
    }

    public Map<String, Class<? extends FeatureFilter>> getAvailableFeatureFilters() {
        return availableFeatureFilters;
    }

}
