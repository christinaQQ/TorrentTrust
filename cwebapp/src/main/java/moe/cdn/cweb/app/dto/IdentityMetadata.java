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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handle == null) ? 0 : handle.hashCode());
        result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
        result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdentityMetadata other = (IdentityMetadata) obj;
        if (handle == null) {
            if (other.handle != null)
                return false;
        } else if (!handle.equals(other.handle))
            return false;
        if (privateKey == null) {
            if (other.privateKey != null)
                return false;
        } else if (!privateKey.equals(other.privateKey))
            return false;
        if (publicKey == null) {
            if (other.publicKey != null)
                return false;
        } else if (!publicKey.equals(other.publicKey))
            return false;
        return true;
    }
}
