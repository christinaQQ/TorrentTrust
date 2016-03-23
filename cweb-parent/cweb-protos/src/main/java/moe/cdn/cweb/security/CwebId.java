package moe.cdn.cweb.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class CwebId {
    public static final int MAX_NUM_BYTES = 20;

    private final byte[] bytes;

    public CwebId(int numBytes, Random random) {
        this(new byte[numBytes]);
        random.nextBytes(bytes);
    }

    public CwebId(Random random) {
        this(MAX_NUM_BYTES, random);
    }

    public CwebId(byte[] bytes) {
        if (bytes.length > MAX_NUM_BYTES) {
            throw new IllegalArgumentException(
                    "Expected array length at most " + MAX_NUM_BYTES + ", but got " + bytes.length);
        }
        this.bytes = bytes;
    }

    public static CwebId fromBase64(String s) {
        return new CwebId(Base64.getDecoder().decode(s));
    }

    public static CwebId fromInt(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) (i >> 16);
        bytes[2] = (byte) (i >> 8);
        bytes[3] = (byte) i;
        return new CwebId(bytes);
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    public byte[] underlyingArray() {
        return bytes;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
