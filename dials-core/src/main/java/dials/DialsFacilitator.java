package dials;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import dials.messages.*;
import dials.model.FeatureModel;
import dials.model.FilterModel;

import java.util.Set;

public class DialsFacilitator extends UntypedActor {

    private ActorRef requester;
    private boolean finalized;

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FeatureStateRequestMessage) {
            handleFeatureStateRequestMessage((FeatureStateRequestMessage) message);
        } else if (message instanceof FilterRetrievalResultMessage) {
            handleFilterRetrievalResultMessage((FilterRetrievalResultMessage) message);
        } else if (message instanceof FilterDispatchResultMessage) {
            handleFilterDispatchResultMessage((FilterDispatchResultMessage) message);
        } else if (message instanceof AbandonMessage) {
            abandon((ContextualMessage) message);
        }
    }

    private void handleFeatureStateRequestMessage(FeatureStateRequestMessage message) {
        requester = sender();

        FeatureModel feature = message.getConfiguration().getRepository().getFeature(message.getExecutionContext().getFeatureName());

        Set<FilterModel> filters = feature.getFilters();

        if (filters == null || filters.isEmpty()) {
            respondToSystem(true, message);
        }

        context().actorOf(Props.create(FilterRetriever.class)).tell(
                new FilterRetrievalRequestMessage(message), self()
        );
    }

    private void handleFilterRetrievalResultMessage(FilterRetrievalResultMessage message) {
        if (message.getFilters().size() == 0) {
            respondToSystem(true, message);
            return;
        }

        context().actorOf(Props.create(FilterDispatcher.class, message.getConfiguration().isFailFastEnabled()))
                .tell(new FilterDispatchRequestMessage(message), self());
    }

    private void handleFilterDispatchResultMessage(FilterDispatchResultMessage message) {
        respondToSystem(message.getResult(), message);
    }

    private void abandon(ContextualMessage message) {
        respondToSystem(false, message);
    }

    private void respondToSystem(boolean state, ContextualMessage message) {
        finalizeExecution(state, message);
        requester.tell(new FeatureStateResultMessage(state), self());
    }

    private void finalizeExecution(boolean state, ContextualMessage message) {
        if (!finalized) {
            message.getExecutionContext().addExecutionStep("Feature State Request Complete")
                    .addExecutionStep("Final Result - " + (state ? "Success" : "Failed"));

            message.getExecutionContext().setExecuted(state);
            message.registerFeatureAttempt(message.getExecutionContext().getFeatureName(), state);

            context().system().actorSelection("/user/ExecutionRegistry").tell(new ContextualMessage(message), self());

            finalized = true;
        }
    }
}
