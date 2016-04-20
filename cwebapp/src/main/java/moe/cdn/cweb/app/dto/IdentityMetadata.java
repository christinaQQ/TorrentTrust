package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import moe.cdn.cweb.SecurityProtos.KeyPair;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityMetadata extends KeyHash {

    private String handle;

    public IdentityMetadata() {}

    public IdentityMetadata(String handle, KeyPair keyPair) {
        super(keyPair.getPublicKey().getHash(), keyPair.getPrivateKey().getHash());
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
