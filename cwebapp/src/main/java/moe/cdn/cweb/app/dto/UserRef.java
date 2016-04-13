package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author davix
 */
@XmlRootElement
public class UserRef {
    // FIXME @XmlJavaTypeAdapter(BytesBase64StringAdapter.class)
    private String publicKeyBase64;

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }
}
