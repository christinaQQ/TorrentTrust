package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.security.UnsupportedAlgorithmException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * Utilities for signing and verification of signatures
 *
 * @author jim
 */
public final class SignatureUtils {
    // Please don't instantiate this class
    private SignatureUtils() {
    }

    public static boolean validateMessage(Signature signature, byte[] message) {
        try {
            java.security.Signature verifier = java.security.Signature.getInstance("SHA256withRSA");

            verifier.initVerify(KeyUtils.importPublicKey(signature.getPublicKey()));
            verifier.update(message);
            return verifier.verify(signature.getSignature().toByteArray());
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedAlgorithmException();
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Key decoding failed.", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Lol What's a SignatureException", e);
        }
    }

    public static Signature signMessage(KeyPair keypair, byte[] message) {
        try {
            java.security.Signature signer = java.security.Signature.getInstance("SHA256withRSA");
            signer.initSign(KeyUtils.importPrivateKey(keypair.getPrivateKey()));
            signer.update(message);
            return Signature.newBuilder().setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                    .setPublicKey(keypair.getPublicKey())
                    .setSignature(ByteString.copyFrom(signer.sign())).build();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedAlgorithmException();
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Key decoding failed.", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Lol What's a SignatureException", e);
        }
    }

    public static Signature signMessage(KeyPair keypair, Message message) {
        return signMessage(keypair, message.toByteArray());
    }

    public static boolean validateMessage(Signature signature, Message message) {
        return validateMessage(signature, message.toByteArray());
    }
}
