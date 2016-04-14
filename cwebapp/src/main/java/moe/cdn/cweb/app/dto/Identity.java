package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author davix
 */
@XmlRootElement
public class Identity {
    private String idBase64;
    private String handle;
    private KeyPairBase64 keyPair;

    public Identity() {}

    public Identity(String idBase64, String handle, KeyPairBase64 keyPair) {
        this.idBase64 = idBase64;
        this.handle = handle;
        this.keyPair = keyPair;
    }
}
