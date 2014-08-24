package dials.messages;

public class DialableFilterApplicationMessage extends ContextualMessage {

    private String filterName;

    public DialableFilterApplicationMessage(ContextualMessage message, String filterName) {
        super(message);
        this.filterName = filterName;
    }

    public String getFilterName() {
        return filterName;
    }
}
