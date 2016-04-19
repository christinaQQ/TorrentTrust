package moe.cdn.cweb.torrentutils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class FileIdExtractorTest {

    @Test
    public void test521TextbookSHA1() throws NoSuchAlgorithmException, IOException {
        File f = new File("src/test/resources/test.torrent");
        assertEquals("6bf4fe3317bd2a9729a1df7f9e37a5b749869f32",
                FileIdExtractor.getIdFromTorrentFile(f));
    }

    @Test
    public void testThrowsIOExceptionWhenFileIsntThere() throws NoSuchAlgorithmException {
        File f = new File("src/test/resources/idontexist.torrent");
        try {
            FileIdExtractor.getIdFromTorrentFile(f);
            fail("Should have thrown exception.");
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }

    }

    @Test
    public void testMagnetUriParsing() throws URISyntaxException, UnsupportedEncodingException {
        String uri = "magnet:?xt=urn:btih:6bf4fe3317bd2a9729a1df7f9e37a5b749869f32&dn=Artificial"
                + "%20Intelligence%20A%20Modern%20Approach%203rd%20Edition"
                + ".pdf&tr=udp%3A%2F%2Fopen.demonii.com%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker"
                + ".publicbt.com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.openbittorrent"
                + ".com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.istole.it%3A80%2Fannounce";
        assertEquals("6bf4fe3317bd2a9729a1df7f9e37a5b749869f32",
                FileIdExtractor.getIdFromMagnetLink(uri));
    }

    @Test
    public void testTorrentAndMagnetMatch()
            throws URISyntaxException, NoSuchAlgorithmException, IOException {
        String uri = "magnet:?xt=urn:btih:6bf4fe3317bd2a9729a1df7f9e37a5b749869f32&dn=Artificial"
                + "%20Intelligence%20A%20Modern%20Approach%203rd%20Edition"
                + ".pdf&tr=udp%3A%2F%2Fopen.demonii.com%3A1337%2Fannounce&tr=udp%3A%2F%2Ftracker"
                + ".publicbt.com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.openbittorrent"
                + ".com%3A80%2Fannounce&tr=udp%3A%2F%2Ftracker.istole.it%3A80%2Fannounce";
        File f = new File("src/test/resources/test.torrent");
        assertEquals(FileIdExtractor.getIdFromTorrentFile(f),
                FileIdExtractor.getIdFromMagnetLink(uri));
    }
}
