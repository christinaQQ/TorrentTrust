package moe.cdn.cweb.torrentutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class FileIdExtractor {
	public static String getIdFromMagnetLink(final String magnetLink) throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(magnetLink);
		if (!uri.toString().contains("?")) {
			throw new IllegalArgumentException("No query");
		}
	    String query = uri.toString().substring(uri.toString().indexOf("?") + 1);
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
	        String val = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
	        if (key.equals("xt")) {
	        	if (!val.startsWith("urn:btih:")) {
	        		throw new IllegalArgumentException("Expected urn:btih:");
	        	} else {
	        		return val.substring("urn:btih:".length());
	        	}
	        }
	    }		
	    throw new IllegalArgumentException("No xt");
	}
	
	public static String getIdFromTorrentFile(final File torrentfile)
			throws NoSuchAlgorithmException, IOException {
		MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
		InputStream input = null;

		try {
			input = new FileInputStream(torrentfile);
			StringBuilder builder = new StringBuilder();
			while (!builder.toString().endsWith("4:info")) {
				builder.append((char) input.read()); // It's ASCII anyway.
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (int data; (data = input.read()) > -1; output.write(data)) {
				// Nothing! java!
			}
			sha1.update(output.toByteArray(), 0, output.size() - 1);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException ignore) {
				}
		}

		// Convert the byte to hex format
		try (Formatter formatter = new Formatter()) {
			for (final byte b : sha1.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}
}
