package dials.messages;

import akka.actor.ActorRef;

import java.util.List;

public class FilterDispatchRequestMessage extends ContextualMessage {

    private List<ActorRef> filters;

    public FilterDispatchRequestMessage(FilterRetrievalResultMessage message) {
        super(message);
        this.filters = message.getFilters();
    }

    public List<ActorRef> getFilters() {
        return filters;
    }
}
