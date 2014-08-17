package dials.filter;

public class FilterDataException extends Exception {

    public FilterDataException() {
        super();
    }

    public FilterDataException(String message) {
        super(message);
    }

    public FilterDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilterDataException(Throwable cause) {
        super(cause);
    }

    protected FilterDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
