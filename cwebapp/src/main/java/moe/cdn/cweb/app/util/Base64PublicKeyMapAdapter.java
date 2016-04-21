package moe.cdn.cweb.app.util;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.protobuf.InvalidProtocolBufferException;

import moe.cdn.cweb.SecurityProtos;

/**
 * @author davix
 */
public class Base64PublicKeyMapAdapter<V> extends XmlAdapter<Map<String, V>, Map<SecurityProtos.Key, V>> {

    @Override
    public Map<SecurityProtos.Key, V> unmarshal(Map<String, V> v) throws InvalidProtocolBufferException {
        Map<SecurityProtos.Key, V> result = new HashMap<>(v.size());
        for (Map.Entry<String, V> kv : v.entrySet()) {
            result.put(SecurityProtos.Key.parseFrom(Base64.getDecoder().decode(kv.getKey())),
                    kv.getValue());
        }
        return result;
    }

    @Override
    public Map<String, V> marshal(Map<SecurityProtos.Key, V> v) {
        return v.entrySet().stream()
                .collect(Collectors.toMap(
                        kv -> Base64.getEncoder().encodeToString(kv.getKey().toByteArray()),
                        Map.Entry::getValue));
    }
}
