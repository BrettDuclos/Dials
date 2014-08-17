package dials.messages;

import dials.execution.ExecutionContext;

public class RegisterExecutionMessage {

    private ExecutionContext executionContext;

    public RegisterExecutionMessage(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }
}
