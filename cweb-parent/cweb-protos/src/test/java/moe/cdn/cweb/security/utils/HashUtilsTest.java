package moe.cdn.cweb.security.utils;

import com.google.protobuf.ByteString;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HashUtilsTest {
    private static final byte[] TEST_STRING = "Hello World!".getBytes();
    private static final byte[] SHA1_TEST_STRING =
            asByteArray(new int[] {0x2e, 0xf7, 0xbd, 0xe6, 0x08, 0xce, 0x54, 0x04, 0xE9, 0x7d, 0x5f,
                    0x04, 0x2f, 0x95, 0xf8, 0x9f, 0x1c, 0x23, 0x28, 0x71});
    private static final byte[] SHA256_TEST_STRING = asByteArray(new int[] {0x7f, 0x83, 0xb1, 0x65,
            0x7f, 0xf1, 0xfc, 0x53, 0xb9, 0x2d, 0xc1, 0x81, 0x48, 0xa1, 0xd6, 0x5d, 0xfc, 0x2d,
            0x4b, 0x1f, 0xa3, 0xd6, 0x77, 0x28, 0x4a, 0xdd, 0xd2, 0x00, 0x12, 0x6d, 0x90, 0x69});

    /**
     * Makes an integer array into a byte array
     *
     * @param array
     * @return
     */
    private static byte[] asByteArray(int[] array) {
        byte[] copy = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            copy[i] = (byte) array[i];
        }
        return copy;
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSha1() {
        assertArrayEquals(SHA1_TEST_STRING, HashUtils.sha1(TEST_STRING));
    }

    @Test
    public void testSha256() {
        assertArrayEquals(SHA256_TEST_STRING, HashUtils.sha256(TEST_STRING));
    }

    @Test
    public void testSha256Hash() {
        assertEquals(
                Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA_256)
                        .setHashValue(ByteString.copyFrom(SHA256_TEST_STRING)).build(),
                HashUtils.hashOf(TEST_STRING));
    }

    @Test
    public void testSha1Hash() {
        assertEquals(
                Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA_1)
                        .setHashValue(ByteString.copyFrom(SHA1_TEST_STRING)).build(),
                HashUtils.sha1HashOf(TEST_STRING));
    }
}
