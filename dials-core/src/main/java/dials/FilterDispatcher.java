package dials;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import dials.messages.*;

public class FilterDispatcher extends UntypedActor {

    private int filterCount;
    private int responseCount;
    private boolean failFast;
    private boolean failed;
    private boolean resultSent;

    public FilterDispatcher(boolean failFast) {
        filterCount = 0;
        responseCount = 0;
        this.failFast = failFast;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ContextualMessage && !((ContextualMessage) message).isAbandoned()) {
            if (message instanceof FilterDispatchRequestMessage) {
                handleFilterDispatchRequestMessage((FilterDispatchRequestMessage) message);
            } else if (message instanceof FilterResultMessage) {
                handleFilterResultMessage((FilterResultMessage) message);
            }
        }
    }

    private void handleFilterDispatchRequestMessage(FilterDispatchRequestMessage message) {
        filterCount = message.getFilters().size();

        for (ActorRef filter : message.getFilters()) {
            filter.tell(new FilterRequestMessage(message), self());
        }
    }

    private void handleFilterResultMessage(FilterResultMessage message) {
        failed |= !message.getResult();

        if (!resultSent) {
            if (failed && failFast) {
                message.getExecutionContext().addExecutionStep("Filter Dispatcher Failed Fast");
                sendResult(new FilterDispatchResultMessage(false, message));
            } else {
                if (++responseCount == filterCount) {
                    sendResult(new FilterDispatchResultMessage(!failed, message));
                }
            }
        }
    }

    private void sendResult(FilterDispatchResultMessage message) {
        if (!resultSent) {
            context().actorSelection("..").tell(message, self());
            resultSent = true;
        }
    }

}
