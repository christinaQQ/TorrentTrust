package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {
    // Please don't instantiate this class
    private HashUtils() {}

    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    public static Hash hashOf(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA256)
                    .setHashValue(ByteString.copyFrom(md.digest(bytes))).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    public static Hash hashOf(String bytes) {
        return hashOf(bytes.getBytes());
    }
}
