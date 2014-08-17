package dials.execution;

import java.io.FileWriter;

public class FileBasedExecutionContextRecorder implements ExecutionContextRecorder {

    private String filePath;

    public FileBasedExecutionContextRecorder(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void recordExecutionContext(ExecutionContext executionContext) throws Exception {
        FileWriter writer = new FileWriter(filePath);
        writer.write(executionContext.toString());
        writer.close();
    }

}
