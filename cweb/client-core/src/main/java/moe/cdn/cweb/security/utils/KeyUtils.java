package moe.cdn.cweb.security.utils;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;

/**
 * Utilities for creation and manipulation of keys.
 *
 * @author jim
 */
public final class KeyUtils {
    // Please don't instantiate this class
    private KeyUtils() {}

    /**
     * Builds a cweb-proto keypair from public and private Java keys.
     * 
     * @param publicKey
     * @param privateKey
     * @return
     */
    public static final KeyPair fromKeys(PublicKey publicKey, PrivateKey privateKey) {
        return KeyPair.newBuilder()
                .setPublicKey(Key.newBuilder().setRaw(ByteString.copyFrom(publicKey.getEncoded()))
                        .setHash(HashUtils.hashOf(publicKey.getEncoded())).build())
                .setPrivateKey(Key.newBuilder().setRaw(ByteString.copyFrom(privateKey.getEncoded()))
                        .setHash(HashUtils.hashOf(privateKey.getEncoded())).build())
                .build();
    }

    /**
     * Generates a new cweb-proto keypair.
     * 
     * @return
     */
    public static final KeyPair generateKeypair() {
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

    public static final PublicKey importPublicKey(Key publicKey) {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey.getRaw().toByteArray());
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not access RSA algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Key did not follow X509 spec", e);
        }
    }

    public static final PrivateKey importPrivateKey(Key privateKey) {
        X509EncodedKeySpec privateKeySpec =
                new X509EncodedKeySpec(privateKey.getRaw().toByteArray());
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not access RSA algorithm", e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("Key did not follow X509 spec", e);
        }
    }

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
            throw new RuntimeException("Cannot translate non-RSA private key to public key.");
        }
    }
}
