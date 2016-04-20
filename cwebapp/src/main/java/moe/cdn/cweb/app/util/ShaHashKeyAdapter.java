package moe.cdn.cweb.app.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.security.utils.HashUtils;

public class ShaHashKeyAdapter extends XmlAdapter<String, Hash> {

    @Override
    public Hash unmarshal(String v) throws InvalidProtocolBufferException {
        return Hash.newBuilder().setHashValue(ByteString.copyFrom(HashUtils.fromHexEncoding(v)))
                .setAlgorithm(HashAlgorithm.SHA_256).build();
    }

    @Override
    public String marshal(Hash v) {
        if (!v.getAlgorithm().equals(HashAlgorithm.SHA_256)) {
            throw new RuntimeException("Will not serialize a non SHA256 hash");
        }
        return HashUtils.toHexEncoding(v.getHashValue().toByteArray());
    }
}
