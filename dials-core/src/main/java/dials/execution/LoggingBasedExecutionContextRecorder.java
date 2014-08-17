package dials.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingBasedExecutionContextRecorder implements ExecutionContextRecorder {

    public static String INFO = "info";
    public static String WARN = "warn";
    public static String TRACE = "trace";
    public static String DEBUG = "debug";
    public static String ERROR = "error";

    private Logger logger = LoggerFactory.getLogger(LoggingBasedExecutionContextRecorder.class);

    private String logLevel;

    public LoggingBasedExecutionContextRecorder(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public void recordExecutionContext(ExecutionContext executionContext) throws Exception {
        if (logLevel.equals(INFO)) {
            logger.info(executionContext.toString());
        } else if (logLevel.equals(WARN)) {
            logger.warn(executionContext.toString());
        } else if (logLevel.equals(ERROR)) {
            logger.error(executionContext.toString());
        } else if (logLevel.equals(TRACE)) {
            logger.trace(executionContext.toString());
        } else if (logLevel.equals(DEBUG)) {
            logger.debug(executionContext.toString());
        }
    }
}
