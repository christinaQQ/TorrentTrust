package moe.cdn.cweb.security;

public class KeyLookupServiceException extends RuntimeException {

    private static final long serialVersionUID = 4654662571355405226L;

    public KeyLookupServiceException(String message) {
        super(message);
    }

    public KeyLookupServiceException(String message, Exception e) {
        super(message, e);
    }
}
