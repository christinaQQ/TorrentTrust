package moe.cdn.cweb.app.api.exceptions;

public class KeyPairConflictException extends ConflictException {

    private static final long serialVersionUID = -1647030059585035904L;

    public KeyPairConflictException() {
    }

    public KeyPairConflictException(String message) {
        super(message);
    }

    public KeyPairConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyPairConflictException(Throwable cause) {
        super(cause);
    }

    public KeyPairConflictException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
