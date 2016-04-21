package moe.cdn.cweb.app.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import moe.cdn.cweb.security.CwebId;

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
