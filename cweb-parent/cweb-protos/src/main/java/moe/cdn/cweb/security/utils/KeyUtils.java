package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Key.KeyType;
import moe.cdn.cweb.SecurityProtos.KeyPair;

import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Utilities for creation and manipulation of keys.
 *
 * @author jim
 */
public final class KeyUtils {
    /**
     * This class should not be instantiated.
     */
    private KeyUtils() {}

    /**
     * Builds a cweb-proto keypair from public and private Java keys.
     *
     * @param publicKey
     * @param privateKey
     * @return
     */
    public static final KeyPair fromKeys(PublicKey publicKey, PrivateKey privateKey) {
        return KeyPair.newBuilder().setPublicKey(fromKey(publicKey))
                .setPrivateKey(fromKey(privateKey)).build();
    }

    /**
     * Build a key proto based on a {@link PublicKey}
     * 
     * @param publicKey
     * @return
     */
    public static final Key fromKey(PublicKey publicKey) {
        return Key.newBuilder().setType(Key.KeyType.PUBLIC)
                .setHash(HashUtils.hashOf(publicKey.getEncoded()))
                .setRaw(ByteString.copyFrom(publicKey.getEncoded())).build();
    }

    /**
     * Build a key proto based on a {@link PrivateKey}
     * 
     * @param privateKey
     * @return
     */
    public static final Key fromKey(PrivateKey privateKey) {
        return Key.newBuilder().setType(Key.KeyType.PRIVATE)
                .setHash(HashUtils.hashOf(privateKey.getEncoded()))
                .setRaw(ByteString.copyFrom(privateKey.getEncoded())).build();
    }

    /**
     * Generates a new cweb-proto keypair.
     *
     * @return cweb protocol buffer keypair
     * @throws RuntimeException if RSA was not supported
     * @throws RuntimeException if SHA1PRNG was not available
     */
    public static final KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
            java.security.KeyPair keypair = keyGen.generateKeyPair();
            return fromKeys(keypair.getPublic(), keypair.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Some secure algorithm was missing.", e);
        }
    }

    /**
     * Imports a cweb-proto based key as a {@link PublicKey}
     * 
     * @param publicKey proto message representing key
     * @return the RSA public key representing this proto based key
     * @throws RuntimeException if RSA was not supported
     * @throws IllegalArgumentException if the key was not a public key
     * @throws IllegalArgumentException if the key was not encoded in X509 spec
     */
    public static final PublicKey importPublicKey(Key publicKey) {
        if (!KeyType.PUBLIC.equals(publicKey.getType())) {
            throw new IllegalArgumentException(
                    "Attempted to import a non-public key as a public key.");
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getRaw().toByteArray());
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not access RSA algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Key did not follow X509 spec", e);
        }
    }

    /**
     * Imports a cweb-proto based private key as a {@link PrivateKey}
     * 
     * @param privateKey proto message representing key
     * @return the RSA private key representing this proto based key
     * @throws RuntimeException if RSA was not supported
     * @throws IllegalArgumentException if the key was not encoded in PKCS8
     */
    public static final PrivateKey importPrivateKey(Key privateKey) {
        if (!KeyType.PRIVATE.equals(privateKey.getType())) {
            throw new IllegalArgumentException(
                    "Attempted to import a non-private key as a private key.");
        }
        PKCS8EncodedKeySpec privateKeySpec =
                new PKCS8EncodedKeySpec(privateKey.getRaw().toByteArray());
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not access RSA algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Key did not follow PKCS8 spec", e);
        }
    }

    /**
     * Derives an RSA {@link PublicKey} from an RSA {@link PrivateKey}
     * 
     * @param privateKey RSA private key
     * @return RSA public key paired with this private key
     */
    public static final PublicKey toPublicKey(PrivateKey privateKey) {
        if (privateKey instanceof RSAPrivateCrtKey) {
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) privateKey;
            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPublicKeySpec publicKeySpec =
                        new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
                return kf.generatePublic(publicKeySpec);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Could not access RSA algorithm", e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException("Key Spec did not work", e);
            }

        } else {
            throw new IllegalArgumentException(
                    "Cannot translate non-RSA private key to public key.");
        }
    }
}
