package dials.execution;

public interface ExecutionContextRecorder {

    /**
     * Consume the execution context provided through the execution of a feature state request.
     */
    void recordExecutionContext(ExecutionContext executionContext) throws Exception;
}
