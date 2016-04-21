package moe.cdn.cweb.app.util;

import java.math.BigInteger;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringHashValueAdapter extends XmlAdapter<String, byte[]> {
    @Override
    public byte[] unmarshal(String v) throws Exception {
        return new BigInteger(v, 16).toByteArray();
    }

    @Override
    public String marshal(byte[] v) throws Exception {
        return new BigInteger(v).toString(16);
    }
}
