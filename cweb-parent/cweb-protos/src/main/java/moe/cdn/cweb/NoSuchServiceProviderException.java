package moe.cdn.cweb;

/**
 * Thrown to indicate that the service provider being requested does not exist.
 *
 * @author davix
 */
public class NoSuchServiceProviderException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 788L;

    public NoSuchServiceProviderException() {
    }

    public NoSuchServiceProviderException(String message) {
        super(message);
    }

    public NoSuchServiceProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchServiceProviderException(Throwable cause) {
        super(cause);
    }

    public NoSuchServiceProviderException(String message,
                                          Throwable cause,
                                          boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
