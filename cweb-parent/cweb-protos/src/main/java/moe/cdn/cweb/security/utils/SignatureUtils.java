package moe.cdn.cweb.security.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.security.exceptions.MalformedSignatureException;

/**
 * Utilities for signing and verification of signatures
 *
 * @author jim
 */
public final class SignatureUtils {
    private static final Logger logger = LogManager.getLogger();

    // Please don't instantiate this class
    private SignatureUtils() {}

    private static java.security.Signature getDefaultSignatureAlgorithm() {
        try {
            return java.security.Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            assert false : e;
            throw new RuntimeException(e);
        }
    }

    private static boolean isDefaultSignatureAlgorithm(Signature signature) {
        return SignatureAlgorithm.SHA_256_WITH_RSA.equals(signature.getAlgorithm());
    }

    /**
     * Validates a byte array against a signature
     *
     * @param signature signature proto
     * @param message message byte array to verify
     * @return boolean indicator of verification success
     * @throws MalformedSignatureException if the signature does not contain a
     *         public key
     * @throws IllegalArgumentException if the signature is not signed with
     *         SHA256withRSA
     */
    public static boolean validateMessage(Signature signature, byte[] message) {
        if (!signature.hasPublicKey()) {
            throw new MalformedSignatureException(signature);
        }
        if (!isDefaultSignatureAlgorithm(signature)) {
            logger.debug("Unsupported signature algorithm: {}", signature.getAlgorithm());
            return false;
        }
        try {
            java.security.Signature verifier = getDefaultSignatureAlgorithm();
            verifier.initVerify(KeyUtils.importPublicKey(signature.getPublicKey()));
            verifier.update(message);
            return verifier.verify(signature.getSignature().toByteArray());
        } catch (InvalidKeyException e) {
            logger.catching(Level.DEBUG, e);
            return false;
        } catch (SignatureException e) {
            logger.catching(Level.DEBUG, e);
            return false;
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
    public static Signature signMessage(KeyPair keypair, byte[] message)
            throws InvalidKeyException, SignatureException {
        if (!keypair.hasPrivateKey()) {
            throw new IllegalArgumentException("Keypair must contain private key");
        }
        java.security.Signature signer = getDefaultSignatureAlgorithm();
        PrivateKey privateKey = KeyUtils.importPrivateKey(keypair.getPrivateKey());
        signer.initSign(privateKey);
        signer.update(message);
        return Signature.newBuilder().setAlgorithm(SignatureAlgorithm.SHA_256_WITH_RSA)
                .setPublicKey(keypair.hasPublicKey() ? keypair.getPublicKey()
                        : KeyUtils.fromKey(KeyUtils.toPublicKey(privateKey)))
                .setSignature(ByteString.copyFrom(signer.sign())).build();
    }

    /**
     * Signs a {@link Message} with a {@link KeyPair}
     *
     * @param keyPair key pair with which to sign
     * @param message protocol buffer based message to sign
     * @return signature object
     */
    public static Signature signMessage(KeyPair keyPair, Message message)
            throws SignatureException, InvalidKeyException {
        return signMessage(keyPair, message.toByteArray());
    }

    public static Signature signMessageUnchecked(KeyPair keyPair, Message message) {
        try {
            return signMessage(keyPair, message);
        } catch (SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
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
