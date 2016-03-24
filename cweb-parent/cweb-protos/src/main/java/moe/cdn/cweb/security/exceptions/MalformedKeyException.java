package moe.cdn.cweb.security.exceptions;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.security.utils.Representations;

public class MalformedKeyException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public MalformedKeyException(String type, Key key) {
        super(String.format("Malformed key [expected format: %s]: %s", type,
                Representations.asString(key)));
    }

    public MalformedKeyException(String type, Key key, Throwable e) {
        super(String.format("Malformed key [expected format: %s]: %s", type,
                Representations.asString(key)), e);
    }

    public MalformedKeyException(Key key) {
        super(String.format("Malformed key: %s", Representations.asString(key)));
    }

    public MalformedKeyException(Key key, Throwable e) {
        super(String.format("Malformed key: %s", Representations.asString(key)), e);
    }
}
