package moe.cdn.cweb.app.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Base64;

/**
 * @author davix
 */
public class Base64BytesAdapter extends XmlAdapter<String, byte[]> {

    @Override
    public byte[] unmarshal(String v) {
        return Base64.getDecoder().decode(v);
    }

    @Override
    public String marshal(byte[] v) {
        return Base64.getEncoder().encodeToString(v);
    }
}
