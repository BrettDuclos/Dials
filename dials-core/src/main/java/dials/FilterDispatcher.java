package dials;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import dials.messages.FilterDispatchRequestMessage;
import dials.messages.FilterDispatchResultMessage;
import dials.messages.FilterRequestMessage;
import dials.messages.FilterResultMessage;

public class FilterDispatcher extends UntypedActor {

    private int filterCount;
    private int responseCount;
    private boolean failFast;
    private boolean failed;
    private boolean resultSent;

    public FilterDispatcher() {
        filterCount = 0;
        responseCount = 0;
        failFast = Dials.isFailFastEnabled();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterDispatchRequestMessage) {
            handleFilterDispatchRequestMessage((FilterDispatchRequestMessage) message);
        } else if (message instanceof FilterResultMessage) {
            handleFilterResultMessage((FilterResultMessage) message);
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
                return;
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
