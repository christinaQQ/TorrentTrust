package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;

/**
 * Utilities for signing and verification of signatures
 *
 * @author jim
 */
public final class SignatureUtils {
    // Please don't instantiate this class
    private SignatureUtils() {
    }
    
    public static final Signature signMessage(KeyPair keypair, byte[] message) {
        return Signature.newBuilder().setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                .setPublicKey(keypair.getPublicKey())
                .setSignature(ByteString.copyFrom(new byte[] {})).build();
    }
}
