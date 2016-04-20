package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.app.util.ShaHashKeyAdapter;

/**
 * @author davix
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyHash {
    @XmlJavaTypeAdapter(ShaHashKeyAdapter.class)
    private Hash publicKeyHash;

    @XmlJavaTypeAdapter(ShaHashKeyAdapter.class)
    private Hash privateKeyHash;

    public KeyHash() {}

    public KeyHash(Hash publicKeyHash, Hash privateKeyHash) {
        this.privateKeyHash = privateKeyHash;
        this.publicKeyHash = publicKeyHash;
    }

    public static KeyHash fromKeyPair(KeyPair keyPair) {
        return new KeyHash(keyPair.getPublicKey().getHash(), keyPair.getPrivateKey().getHash());
    }

    public Hash getPrivateKeyHash() {
        return privateKeyHash;
    }

    public void setPrivateKeyHash(Hash privateKeyHash) {
        this.privateKeyHash = privateKeyHash;
    }

    public Hash getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKey(Hash publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }
}
