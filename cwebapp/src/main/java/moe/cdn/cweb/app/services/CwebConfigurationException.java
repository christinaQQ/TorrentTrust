package moe.cdn.cweb.app.services;

public class CwebConfigurationException extends RuntimeException {
    public CwebConfigurationException() {
    }

    public CwebConfigurationException(String message) {
        super(message);
    }

    public CwebConfigurationException(String msg, Throwable cause) {

        super(msg, cause);
    }

    public CwebConfigurationException(Throwable cause) {
        super(cause);
    }

    public CwebConfigurationException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
