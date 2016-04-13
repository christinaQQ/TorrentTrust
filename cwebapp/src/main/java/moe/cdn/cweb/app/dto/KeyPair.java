package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author davix
 */
@XmlRootElement
public class KeyPair {
    private String publicKeyBase64;
    private String privateKeyBase64;

    public KeyPair() {
    }

    public KeyPair(String publicKeyBase64, String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String getPrivateKeyBase64() {
        return privateKeyBase64;
    }

    public void setPrivateKeyBase64(String privateKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
    }
}
