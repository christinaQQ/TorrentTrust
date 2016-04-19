package moe.cdn.cweb.app.util;

import moe.cdn.cweb.security.CwebId;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 */
public class Base64CwebIdAdapter extends XmlAdapter<String, CwebId> {
    @Override
    public CwebId unmarshal(String v) {
        return CwebId.fromBase64(v);
    }

    @Override
    public String marshal(CwebId v) {
        return v.toBase64String();
    }
}
