package moe.cdn.cweb.app.data;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.util.Base64KeyAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author davix
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrustedIdentity {
    private String name;
    @XmlJavaTypeAdapter(Base64KeyAdapter.class)
    private SecurityProtos.Key publicKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SecurityProtos.Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(SecurityProtos.Key publicKey) {
        this.publicKey = publicKey;
    }
}
