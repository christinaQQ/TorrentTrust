package moe.cdn.cweb.security.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.security.exceptions.UnsupportedAlgorithmException;

/**
 * Utilities for signing and verification of signatures
 *
 * @author jim
 */
public final class SignatureUtils {
    // Please don't instantiate this class
    private SignatureUtils() {}

    /**
     * Validates a byte array against a signature
     * 
     * @param signature signature proto
     * @param message message byte array to verify
     * @return boolean indicator of verification success
     */
    public static boolean validateMessage(Signature signature, byte[] message) {
        if (!signature.hasPublicKey()) {
            throw new IllegalArgumentException("Malformed signature: "
                    + Representations.asString(signature) + ". No public key.");
        }
        if (!Signature.SignatureAlgorithm.SHA256withRSA.equals(signature.getAlgorithm())) {
            throw new IllegalArgumentException(
                    "Signature algorithm " + signature.getAlgorithmValue() + " not recognized.");
        }
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

    /**
     * Signs a byte array message
     * 
     * @param keypair keypair used to sign the message (only the private key is
     *        used)
     * @param message message to be signed
     * @return signature proto containing signature for message
     * @throws IllegalArgumentException if the keypair does not contain a
     *         private key
     */
    public static Signature signMessage(KeyPair keypair, byte[] message) {
        if (!keypair.hasPrivateKey()) {
            throw new IllegalArgumentException("Keypair must contain private key");
        }
        try {
            java.security.Signature signer = java.security.Signature.getInstance("SHA256withRSA");
            PrivateKey privateKey = KeyUtils.importPrivateKey(keypair.getPrivateKey());
            signer.initSign(privateKey);
            signer.update(message);
            return Signature.newBuilder().setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                    .setPublicKey(keypair.hasPublicKey() ? keypair.getPublicKey()
                            : KeyUtils.fromKey(KeyUtils.toPublicKey(privateKey)))
                    .setSignature(ByteString.copyFrom(signer.sign())).build();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedAlgorithmException();
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Key decoding failed", e);
        } catch (SignatureException e) {
            throw new RuntimeException("LOL: What's a SignatureException", e);
        }
    }

    /**
     * Signs a {@link Message} with a {@link KeyPair}
     * 
     * @param keypair keypair to sign with
     * @param message protocol buffer based message to sign
     * @return signature object
     */
    public static Signature signMessage(KeyPair keypair, Message message) {
        return signMessage(keypair, message.toByteArray());
    }

    /**
     * Validates a {@link Message} with a {@link KeyPair}
     * 
     * @param signature signature to validate against
     * @param message protocol buffer based message to sign
     * @return boolean indicator of whether the signature validated correctly
     */
    public static boolean validateMessage(Signature signature, Message message) {
        return validateMessage(signature, message.toByteArray());
    }
}
