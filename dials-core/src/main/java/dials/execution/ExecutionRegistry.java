package dials.execution;

import akka.actor.UntypedActor;
import dials.messages.ContextualMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionRegistry extends UntypedActor {
    private Logger logger = LoggerFactory.getLogger(ExecutionRegistry.class);

    private ExecutionContextRecorder recorder;

    public ExecutionRegistry(ExecutionContextRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ContextualMessage) {
            ContextualMessage contextualMessage = (ContextualMessage) message;

            try {
                recorder.recordExecutionContext(contextualMessage.getExecutionContext());
            } catch (Exception e) {
                logger.warn("Exception occurred recording execution context.", e);
            }
        }
    }
}
