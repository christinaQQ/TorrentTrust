package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.app.util.ShaHashKeyAdapter;

/**
 * @author davix
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserRef {
    @XmlJavaTypeAdapter(ShaHashKeyAdapter.class)
    private Hash publicKey;

    public UserRef() {
    }

    public UserRef(Hash publicKey) {
        this.publicKey = publicKey;
    }

    public Hash getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Hash publicKey) {
        this.publicKey = publicKey;
    }
}
