package moe.cdn.cweb.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import moe.cdn.cweb.security.utils.HashUtils;

/**
 * Class containing identifiers user by Cweb. Identifiers are byte arrays of
 * length {@link CwebId#MAX_NUM_BYTES}.
 * 
 * @author jim
 */
public class CwebId {
    /**
     * Maximum number of bytes allowed in a CwebId
     */
    public static final int MAX_NUM_BYTES = 20;

    private final byte[] bytes;

    /**
     * Constructs a {@link CwebId} with {@code length} bytes of randomness.
     * 
     * @param length
     * @param random
     */
    public CwebId(int length, Random random) {
        this(new byte[length]);
        random.nextBytes(bytes);
    }

    /**
     * Constructs a {@link CwebId} with {@link CwebId#MAX_NUM_BYTES} bytes of
     * randomness.
     * 
     * @param random
     */
    public CwebId(Random random) {
        this(MAX_NUM_BYTES, random);
    }

    /**
     * Constructs a {@link CwebId} from an existing array of bytes
     * 
     * @param bytes existing array of bytes
     * @throws IllegalArgumentException if the input array is longer than
     *         {@link CwebId#MAX_NUM_BYTES}
     */
    public CwebId(byte[] bytes) {
        if (bytes.length > MAX_NUM_BYTES) {
            throw new IllegalArgumentException(
                    "Expected array length at most " + MAX_NUM_BYTES + ", but got " + bytes.length);
        }
        this.bytes = bytes;
    }

    /**
     * Returns a copy of the byte array underlying the data structure.
     * 
     * @return byte array that represents the identifier
     */
    public byte[] toByteArray() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Returns the unsafe underlying array. Modifying this array will change the
     * value of the identifier.
     * 
     * @return underlying byte array
     */
    public byte[] underlyingArray() {
        return bytes;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CwebId other = (CwebId) obj;
        if (!Arrays.equals(bytes, other.bytes))
            return false;
        return true;
    }

    /**
     * Produces a {@link CwebId} from an integer. Produced identifiers are
     * always 4 bytes (32 bits) in size. Integers are treated as being unsigned.
     * 
     * @param i integer value to use as identifier
     * @return identifier that holds the same bytes as the provided integer
     */
    public static CwebId fromInt(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i >> 24);
        bytes[1] = (byte) (i >> 16);
        bytes[2] = (byte) (i >> 8);
        bytes[3] = (byte) i;
        return new CwebId(bytes);
    }

    /**
     * Produces a {@link CwebId} from a long integer. Produced identifiers are
     * always 8 bytes (64 bits) in size. Long integers are treated as being
     * unsigned.
     * 
     * @param i long integer value to use as identifier
     * @return identifier that holds the same bytes as the provided long integer
     */
    public static CwebId fromLong(long i) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (i >> 56);
        bytes[1] = (byte) (i >> 48);
        bytes[2] = (byte) (i >> 40);
        bytes[3] = (byte) (i >> 32);
        bytes[4] = (byte) (i >> 24);
        bytes[5] = (byte) (i >> 16);
        bytes[6] = (byte) (i >> 8);
        bytes[7] = (byte) i;
        return new CwebId(bytes);
    }

    /**
     * Produces a {@link CwebId} from a Base64 encoded string.
     * 
     * @param s Base64 encoded string
     * @return identifier that holds the same bytes as the decoded object
     * @throws IllegalArgumentException if the string decodes into more than
     *         {@link CwebId#MAX_NUM_BYTES} bytes
     */
    public static CwebId fromBase64(String s) {
        return new CwebId(Base64.getDecoder().decode(s));
    }

    /**
     * Produces a {@link CwebId} by taking the SHA1 of an arbitrarily sized byte
     * array.
     * 
     * @param bytes arbitrarily sized byte array
     * @return a {@link CwebId} that is the hash of the input byte array
     */
    public static CwebId fromSha1(byte[] bytes) {
        return new CwebId(HashUtils.sha1(bytes));
    }
}
