package dials.messages;

import dials.DialsSystemConfiguration;
import dials.execution.ExecutionContext;

public class ContextualMessage {

    private ExecutionContext executionContext;
    private DialsSystemConfiguration configuration;

    public ContextualMessage(ExecutionContext executionContext, DialsSystemConfiguration configuration) {
        this.executionContext = executionContext;
        this.configuration = configuration;
    }

    public ContextualMessage(ContextualMessage message) {
        this(message.getExecutionContext(), message.getConfiguration());
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public DialsSystemConfiguration getConfiguration() {
        return configuration;
    }
}
