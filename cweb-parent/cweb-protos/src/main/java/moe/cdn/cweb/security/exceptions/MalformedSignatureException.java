package moe.cdn.cweb.security.exceptions;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.security.utils.Representations;

public class MalformedSignatureException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public MalformedSignatureException(Signature s) {
        super("Malformed Signature: " + Representations.asString(s));
    }

    public MalformedSignatureException(Signature s, Throwable e) {
        super("Malformed Signature: " + Representations.asString(s), e);
    }
}
