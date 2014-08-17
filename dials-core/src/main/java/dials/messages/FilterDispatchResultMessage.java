package dials.messages;

public class FilterDispatchResultMessage extends ContextualMessage {

    private boolean result;

    public FilterDispatchResultMessage(boolean result, ContextualMessage message) {
        super(message);
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
