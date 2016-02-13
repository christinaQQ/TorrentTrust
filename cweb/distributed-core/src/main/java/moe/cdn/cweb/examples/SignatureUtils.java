package moe.cdn.cweb.examples;

import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;

/**
 * This class is only used by the example. Please remove when not needed.
 * 
 * The actual user objects never need to be built into distributed-core
 * 
 * @author jim
 *
 */
final class SignatureUtils {
    static final byte[] sha1(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    static final Hash hashOf(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA256)
                    .setHashvalue(ByteString.copyFrom(md.digest(bytes))).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    static final KeyPair generateKeypair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
            java.security.KeyPair keypair = keyGen.generateKeyPair();
            return KeyPair.newBuilder()
                    .setPublicKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPublic().getEncoded()))
                            .setHash(hashOf(keypair.getPublic().getEncoded())).build())
                    .setPrivateKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPrivate().getEncoded()))
                            .setHash(hashOf(keypair.getPrivate().getEncoded())).build())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Some secure algorithm was missing.", e);
        }
    }

    static final Signature signMessage(KeyPair keypair, byte[] message) {
        return Signature.newBuilder().setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                .setPublicKey(keypair.getPublicKey())
                .setSignature(ByteString.copyFrom(new byte[] {})).build();
    }

    static final SignedUserRecord buildSignedUserRecord(KeyPair keypair, String handle) {
        User userRecord =
                User.newBuilder().setPublicKey(keypair.getPublicKey()).setHandle(handle).build();
        return SignedUserRecord.newBuilder().setUser(userRecord)
                .setSignature(signMessage(keypair, userRecord.toByteArray())).build();
    }
}
