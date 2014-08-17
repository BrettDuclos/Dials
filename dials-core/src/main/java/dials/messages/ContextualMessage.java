package dials.messages;

import dials.execution.ExecutionContext;

public class ContextualMessage {

    private ExecutionContext executionContext;

    public ContextualMessage(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public ContextualMessage(ContextualMessage message) {
        this(message.getExecutionContext());
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }
}
