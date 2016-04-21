package moe.cdn.cweb.security.utils;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Key.KeyType;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.security.exceptions.MalformedKeyException;

/**
 * Utilities for creation and manipulation of keys.
 *
 * @author jim
 */
public final class KeyUtils {
    private static final Logger logger = LogManager.getLogger();

    /**
     * This class should not be instantiated.
     */
    private KeyUtils() {
    }

    /**
     * Builds a cweb-proto public key from the specified bytes
     *
     * @param bytes the raw bytes
     * @return a public {@link Key} proto
     */
    public static Key createPublicKey(byte[] bytes) {
        return Key.newBuilder().setType(KeyType.PUBLIC).setHash(HashUtils.hashOf(bytes))
                .setRaw(ByteString.copyFrom(bytes)).build();
    }

    /**
     * Builds a cweb-proto private key from the specified bytes
     *
     * @param bytes the raw bytes
     * @return a private {@link Key} proto
     */
    public static Key createPrivateKey(byte[] bytes) {
        return Key.newBuilder().setType(KeyType.PRIVATE).setHash(HashUtils.hashOf(bytes))
                .setRaw(ByteString.copyFrom(bytes)).build();
    }

    /**
     * Builds a cweb-proto keypair from public and private Java keys.
     *
     * @param publicKey
     * @param privateKey
     * @return
     */
    public static KeyPair fromKeys(PublicKey publicKey, PrivateKey privateKey) {
        return KeyPair.newBuilder().setPublicKey(fromKey(publicKey))
                .setPrivateKey(fromKey(privateKey)).build();
    }

    /**
     * Build a key proto based on a {@link PublicKey}
     *
     * @param publicKey
     * @return
     */
    public static Key fromKey(PublicKey publicKey) {
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
    public static Key fromKey(PrivateKey privateKey) {
        return Key.newBuilder().setType(Key.KeyType.PRIVATE)
                .setHash(HashUtils.hashOf(privateKey.getEncoded()))
                .setRaw(ByteString.copyFrom(privateKey.getEncoded())).build();
    }

    /**
     * Generates a new cweb-proto keypair.
     *
     * @return cweb protocol buffer keypair
     */
    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = getDefaultKeyPairGenerator();
        keyGen.initialize(2048, new SecureRandom());
        java.security.KeyPair keypair = keyGen.generateKeyPair();
        return fromKeys(keypair.getPublic(), keypair.getPrivate());
    }

    /**
     * @return a key pair generator instance
     * @throws RuntimeException if RSA is not supported
     */
    private static KeyPairGenerator getDefaultKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a key factory instance
     * @throws RuntimeException if RSA is not supported
     */
    private static KeyFactory getDefaultKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            logger.catching(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports a cweb-proto based key as a {@link PublicKey}
     *
     * @param publicKey proto message representing key
     * @return the RSA public key representing this proto based key
     * @throws IllegalArgumentException if the key was not a public key
     * @throws IllegalArgumentException if the key was not encoded in X509 spec
     */
    public static PublicKey importPublicKey(Key publicKey) {
        if (!KeyType.PUBLIC.equals(publicKey.getType())) {
            throw new IllegalArgumentException(
                    "Attempted to import a non-public key as a public key.");
        }
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getRaw().toByteArray());
        try {
            KeyFactory kf = getDefaultKeyFactory();
            return kf.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new MalformedKeyException("X509", publicKey, e);
        }
    }

    /**
     * Imports a cweb-proto based private key as a {@link PrivateKey}
     *
     * @param privateKey proto message representing key
     * @return the RSA private key representing this proto based key
     * @throws IllegalArgumentException if the key was not encoded in PKCS8
     */
    public static PrivateKey importPrivateKey(Key privateKey) {
        if (!KeyType.PRIVATE.equals(privateKey.getType())) {
            throw new IllegalArgumentException(
                    "Attempted to import a non-private key as a private key.");
        }
        PKCS8EncodedKeySpec privateKeySpec =
                new PKCS8EncodedKeySpec(privateKey.getRaw().toByteArray());
        try {
            KeyFactory kf = getDefaultKeyFactory();
            return kf.generatePrivate(privateKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new MalformedKeyException("PKCS8", privateKey, e);
        }
    }

    /**
     * Derives an RSA {@link PublicKey} from an RSA {@link PrivateKey}
     *
     * @param privateKey RSA private key
     * @return RSA public key paired with this private key
     */
    public static PublicKey toPublicKey(PrivateKey privateKey) {
        if (!(privateKey instanceof RSAPrivateCrtKey)) {
            throw new IllegalArgumentException(
                    "Cannot translate non-RSA CRT private key to public key.");
        }

        RSAPrivateCrtKey key = (RSAPrivateCrtKey) privateKey;
        try {
            KeyFactory kf = getDefaultKeyFactory();
            RSAPublicKeySpec publicKeySpec =
                    new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
            return kf.generatePublic(publicKeySpec);
        } catch (InvalidKeySpecException e) {
            logger.catching(e);
            assert false : e;
            throw new RuntimeException(e);
        }
    }
}
