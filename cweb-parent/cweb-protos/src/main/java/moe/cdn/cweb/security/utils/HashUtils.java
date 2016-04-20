package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {
    // Please don't instantiate this class
    private HashUtils() {
    }

    /**
     * Calculates the SHA1 hash of a given byte array.
     *
     * @param bytes input byte array
     * @return sha1 value of the input bytes array. Length is always 20 (160
     * bits)
     */
    public static byte[] sha1(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    /**
     * Calculates the SHA256 hash of a given byte array.
     *
     * @param bytes input byte array
     * @return sha1 value of the input bytes array. Length is always 20 (160
     * bits)
     */
    public static byte[] sha256(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

    /**
     * Calculates the {@link Hash} of a given byte array. Hashes are backed with
     * SHA256.
     *
     * @param bytes input byte array
     * @return sha256 value of the input bytes array packaged as such.
     */
    public static Hash hashOf(byte[] bytes) {
        return Hash.newBuilder()
                .setAlgorithm(HashAlgorithm.SHA_256)
                .setHashValue(ByteString.copyFrom(sha256(bytes)))
                .build();
    }

    /**
     * Calculates the {@link Hash} of a string.
     *
     * @param str string to hash
     * @return sha256 hash value of the input bytes.
     */
    public static Hash hashOf(String str) {
        return hashOf(str.getBytes());
    }

    /**
     * Calculates the {@link Hash} of a given byte array. Hashes are backed with
     * SHA1.
     *
     * @param bytes input byte array
     * @return sha1 value of the input bytes array packaged as such.
     */
    public static Hash sha1HashOf(byte[] bytes) {
        return Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA_1)
                .setHashValue(ByteString.copyFrom(sha1(bytes))).build();
    }

    /**
     * Calculates the {@link Hash} of a string.
     *
     * @param str string to hash
     * @return sha256 hash value of the input bytes.
     */
    public static Hash sha1HashOf(String str) {
        return hashOf(str.getBytes());
    }

    /**
     * Produces a hex encoded representation of the byte array
     *
     * @param bytes
     * @return
     */
    public static String toHexEncoding(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }

    /**
     * Produces a byte array from the hex encoding
     *
     * @param hex
     * @return
     */
    public static byte[] fromHexEncoding(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static Hash hashOf(ByteString byteString) {
        return hashOf(byteString.toByteArray());
    }
}
