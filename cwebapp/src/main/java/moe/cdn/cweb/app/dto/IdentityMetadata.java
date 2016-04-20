package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.app.util.ShaHashKeyAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityMetadata {

    @XmlJavaTypeAdapter(ShaHashKeyAdapter.class)
    private Hash publicKey;

    @XmlJavaTypeAdapter(ShaHashKeyAdapter.class)
    private Hash privateKey;

    private String handle;

    public IdentityMetadata() {}

    public IdentityMetadata(String handle, KeyPair keyPair) {
        this.publicKey = keyPair.getPublicKey().getHash();
        this.privateKey = keyPair.getPrivateKey().getHash();
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public static KeyHash fromKeyPair(KeyPair keyPair) {
        return new KeyHash(keyPair.getPublicKey().getHash(), keyPair.getPrivateKey().getHash());
    }

    public Hash getPrivateKeyHash() {
        return privateKey;
    }

    public void setPrivateKeyHash(Hash privateKeyHash) {
        this.privateKey = privateKeyHash;
    }

    public Hash getPublicKeyHash() {
        return publicKey;
    }

    public void setPublicKey(Hash publicKeyHash) {
        this.publicKey = publicKeyHash;
    }

}
