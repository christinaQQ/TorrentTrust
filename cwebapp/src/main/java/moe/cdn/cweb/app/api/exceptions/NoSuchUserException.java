package moe.cdn.cweb.app.api.exceptions;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.security.utils.Representations;

public class NoSuchUserException extends NoSuchThingException {

    private static final long serialVersionUID = 6904854389758454782L;

    public NoSuchUserException() {}

    public NoSuchUserException(Key publicKey) {
        this(Representations.asString(publicKey));
    }

    public NoSuchUserException(String message) {
        super(message);
    }

    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchUserException(Throwable cause) {
        super(cause);
    }

    public NoSuchUserException(String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
