package moe.cdn.cweb.security.utils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class CwebId {
    public static final int MAX_NUM_BYTES = 20;
    
    private final byte[] bytes;
    
    public CwebId(int length, Random random) {
        this(new byte[length]);
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
