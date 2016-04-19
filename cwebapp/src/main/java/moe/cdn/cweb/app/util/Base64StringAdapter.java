package moe.cdn.cweb.app.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Base64;

/**
 * @author davix
 */
public class Base64StringAdapter extends XmlAdapter<byte[], String> {

    @Override
    public String unmarshal(byte[] v) throws Exception {
        return Base64.getEncoder().encodeToString(v);
    }

    @Override
    public byte[] marshal(String v) throws Exception {
        return Base64.getDecoder().decode(v);
    }
}
