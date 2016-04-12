package moe.cdn.cweb;

public class CwebApiException extends Exception {
    private static final long serialVersionUID = 1L;

    public CwebApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public CwebApiException(String message) {
        super(message);
    }

    public CwebApiException(Throwable cause) {
        super(cause);
    }

}
