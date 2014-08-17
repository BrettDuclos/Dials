package dials.messages;

import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

public class FilterRetrievalResultMessage extends ContextualMessage {

    private List<ActorRef> filters;

    public FilterRetrievalResultMessage(ContextualMessage message) {
        super(message);
        filters = new ArrayList<>();
    }

    public List<ActorRef> getFilters() {
        return filters;
    }

    public void addFilter(ActorRef actor) {
        filters.add(actor);
    }
}
