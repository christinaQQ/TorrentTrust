package moe.cdn.cweb.dht.security;

public class KeyLookupServiceException extends RuntimeException {

    private static final long serialVersionUID = 4654662571355405226L;

    public KeyLookupServiceException(String message) {
        super(message);
    }

    public KeyLookupServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyLookupServiceException(Throwable cause) {
        super(cause);
    }

    public KeyLookupServiceException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public KeyLookupServiceException() {
    }

}
