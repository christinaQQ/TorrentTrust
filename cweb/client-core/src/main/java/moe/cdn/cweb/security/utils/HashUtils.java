package moe.cdn.cweb.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;

public final class HashUtils {
    // Please don't instantiate this class
    private HashUtils() {}

    static final byte[] sha1(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    public static final Hash hashOf(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA256)
                    .setHashvalue(ByteString.copyFrom(md.digest(bytes))).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    public static final Hash hashOf(String bytes) {
        return hashOf(bytes.getBytes());
    }
}
