package moe.cdn.cweb.app.api.exceptions;

public class CwebApiEndPointException extends RuntimeException {
    public CwebApiEndPointException() {
    }

    public CwebApiEndPointException(String message) {
        super(message);
    }

    public CwebApiEndPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public CwebApiEndPointException(Throwable cause) {
        super(cause);
    }

    public CwebApiEndPointException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
