package moe.cdn.cweb.app.dto;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.app.util.KeyAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author davix
 */
@XmlRootElement
public class KeyPairBase64 {
    @XmlJavaTypeAdapter(KeyAdapter.class)
    private SecurityProtos.Key publicKey;

    @XmlJavaTypeAdapter(KeyAdapter.class)
    private SecurityProtos.Key privateKey;

    public KeyPairBase64() {
    }

    public KeyPairBase64(SecurityProtos.Key publicKey, SecurityProtos.Key privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static KeyPairBase64 fromKeyPair(KeyPair keyPair) {
        return new KeyPairBase64(keyPair.getPublicKey(), keyPair.getPrivateKey());
    }

    public SecurityProtos.Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(SecurityProtos.Key privateKey) {
        this.privateKey = privateKey;
    }

    public SecurityProtos.Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(SecurityProtos.Key publicKey) {
        this.publicKey = publicKey;
    }
}
