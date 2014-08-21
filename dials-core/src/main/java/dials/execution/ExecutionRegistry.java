package dials.execution;

import akka.actor.UntypedActor;
import dials.datastore.DataStore;
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

            DataStore dataStore = ((ContextualMessage) message).getConfiguration().getDataStore();
            dataStore.registerAttempt(contextualMessage.getExecutionContext().getFeatureName(),
                    contextualMessage.getExecutionContext().getExecuted());

            try {
                recorder.recordExecutionContext(contextualMessage.getExecutionContext());
            } catch (Exception e) {
                logger.warn("Exception occurred recording execution context.", e);
            }
        }
    }
}
