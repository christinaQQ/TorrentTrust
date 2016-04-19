package moe.cdn.cweb.app.exceptions;

public class NoSuchThingException extends RuntimeException {

    private static final long serialVersionUID = -7714777783118891616L;

    public NoSuchThingException() {
    }

    public NoSuchThingException(String message) {
        super(message);
    }

    public NoSuchThingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchThingException(Throwable cause) {
        super(cause);
    }

    public NoSuchThingException(String message,
                                Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
