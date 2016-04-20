package moe.cdn.cweb.app.api.exceptions;

public class KeyPairRegistrationException extends ConflictException {

    private static final long serialVersionUID = -1647030059585035904L;

    public KeyPairRegistrationException() {
    }

    public KeyPairRegistrationException(String message) {
        super(message);
    }

    public KeyPairRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyPairRegistrationException(Throwable cause) {
        super(cause);
    }

    public KeyPairRegistrationException(String message,
                                        Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
