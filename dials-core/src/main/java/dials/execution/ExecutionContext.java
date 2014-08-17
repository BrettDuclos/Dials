package dials.execution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExecutionContext {

    private SimpleDateFormat sdf;

    private String featureName;
    private boolean executed;
    private List<String> executionSteps;

    public ExecutionContext(String featureName) {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        executionSteps = Collections.synchronizedList(new ArrayList<String>());
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public boolean getExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public List<String> getExecutionSteps() {
        return executionSteps;
    }

    public ExecutionContext addExecutionStep(String step) {
        executionSteps.add(sdf.format(new Date()) + " - " + step);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(new Date()));
        sb.append(" - Feature Name: ");
        sb.append(featureName);
        sb.append("\n");
        sb.append(sdf.format(new Date()));
        sb.append(" - Executed: ");
        sb.append(executed ? "Yes" : "No");
        sb.append("\n");

        for (String step : executionSteps) {
            sb.append(step);
            sb.append("\n");
        }

        return sb.toString();
    }
}
