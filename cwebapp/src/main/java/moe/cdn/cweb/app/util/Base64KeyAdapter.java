package moe.cdn.cweb.app.util;

import java.util.Base64;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.protobuf.InvalidProtocolBufferException;

import moe.cdn.cweb.SecurityProtos;

/**
 * @author davix
 */
public class Base64KeyAdapter extends XmlAdapter<String, SecurityProtos.Key> {

    @Override
    public SecurityProtos.Key unmarshal(String v) throws InvalidProtocolBufferException {
        return SecurityProtos.Key.parseFrom(Base64.getDecoder().decode(v));
    }

    @Override
    public String marshal(SecurityProtos.Key v) {
        return Base64.getEncoder().encodeToString(v.toByteArray());
    }
}
