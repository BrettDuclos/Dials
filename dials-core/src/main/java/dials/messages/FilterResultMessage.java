package dials.messages;

public class FilterResultMessage extends ContextualMessage {

    private boolean result;

    public FilterResultMessage(boolean result, ContextualMessage message) {
        super(message);
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
