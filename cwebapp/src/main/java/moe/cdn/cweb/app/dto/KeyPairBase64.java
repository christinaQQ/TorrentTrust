package moe.cdn.cweb.app.dto;

import java.util.Base64;

import javax.xml.bind.annotation.XmlRootElement;

import moe.cdn.cweb.SecurityProtos.KeyPair;

/**
 * @author davix
 */
@XmlRootElement
public class KeyPairBase64 {
    private String publicKeyBase64;
    private String privateKeyBase64;

    public KeyPairBase64() {}

    public KeyPairBase64(String publicKeyBase64, String privateKeyBase64) {
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

    public static final KeyPairBase64 fromKeyPair(KeyPair keyPair) {
        return new KeyPairBase64(
                Base64.getEncoder().encodeToString(keyPair.getPublicKey().toByteArray()),
                Base64.getEncoder().encodeToString(keyPair.getPrivateKey().toByteArray()));
    }
}
