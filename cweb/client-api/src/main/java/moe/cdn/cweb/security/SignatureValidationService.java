package moe.cdn.cweb.security;

import moe.cdn.cweb.SecurityProtos.Signature;

public interface SignatureValidationService {
    /**
     * Validates a signature's authenticity based on the key lookup service
     *
     * @param signature
     * @return
     */
    boolean validate(byte[] data, Signature signature);
}
