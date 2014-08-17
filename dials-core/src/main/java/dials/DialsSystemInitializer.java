package dials;

import dials.datastore.DataStore;
import dials.execution.ExecutionContextRecorder;
import dials.execution.NoopExecutionContextRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialsSystemInitializer {

    private Logger logger = LoggerFactory.getLogger(DialsSystemInitializer.class);

    private DataStore dataStore;
    private ExecutionContextRecorder executionContextRecorder;
    private boolean failFastEnabled = true;


    public DialsSystemInitializer withDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
        return this;
    }

    public DialsSystemInitializer withExecutionContextRecorder(ExecutionContextRecorder executionContextRecorder) {
        this.executionContextRecorder = executionContextRecorder;
        return this;
    }

    public DialsSystemInitializer withFailFastEnabled(boolean failFastEnabled) {
        this.failFastEnabled = failFastEnabled;
        return this;
    }

    public void initialize() {
        if (dataStore == null) {
            logger.error("A DataStore is required to initialize the Dials system.");
            return;
        }

        if (executionContextRecorder == null) {
            executionContextRecorder = new NoopExecutionContextRecorder();
        }

        Dials.init(this);
    }

    protected DataStore getDataStore() {
        return dataStore;
    }

    protected ExecutionContextRecorder getExecutionContextRecorder() {
        return executionContextRecorder;
    }

    protected boolean isFailFastEnabled() {
        return failFastEnabled;
    }
}
