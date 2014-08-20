package dials.execution;

import java.io.OutputStream;

public class OutputStreamExecutionContextRecorder implements ExecutionContextRecorder {

    private OutputStream os;

    public OutputStreamExecutionContextRecorder(OutputStream os) {
        this.os = os;
    }

    @Override
    public void recordExecutionContext(ExecutionContext executionContext) throws Exception {
        os.write(executionContext.toString().getBytes());
        os.flush();
    }
}
