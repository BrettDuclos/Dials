package dials.filter;

import akka.actor.UntypedActor;
import dials.messages.AbandonMessage;
import dials.messages.ContextualMessage;
import dials.messages.FilterRequestMessage;
import dials.messages.FilterResultMessage;

public abstract class FeatureFilter extends UntypedActor {

    private boolean abandoned;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FilterRequestMessage) {
            FilterRequestMessage requestMessage = (FilterRequestMessage) message;

            if (!abandoned) {
                boolean passedFilter = filter();
                requestMessage.getExecutionContext()
                        .addExecutionStep((passedFilter ? "Passed Filter" : "Failed Filter") + " - " + getClass().getSimpleName());
                sender().tell(new FilterResultMessage(passedFilter, requestMessage), self());
            }
        }
    }

    protected void recordSuccessfulDataApply(ContextualMessage message, String dataName) {
        message.getExecutionContext().addExecutionStep("Filter " + getClass().getSimpleName() + " Successfully Applied Data - " + dataName);
    }

    protected void recordUnsuccessfulDataApply(ContextualMessage message, String dataName, boolean abandon, String errorMessage) {
        message.getExecutionContext().addExecutionStep(errorMessage);
        message.getExecutionContext().addExecutionStep("Filter " + getClass().getSimpleName() + " Failed To Apply Data - " + dataName);

        if (abandon) {
            abandon(message);
        }
    }

    protected void abandon(ContextualMessage message) {
        context().actorSelection("../../").tell(new AbandonMessage(message), self());
        abandoned = true;
    }

    public abstract boolean filter();


    //For testing purposes.
    public boolean getAbandoned() {
        return abandoned;
    }
}
