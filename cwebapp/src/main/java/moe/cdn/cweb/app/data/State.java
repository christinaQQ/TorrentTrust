package moe.cdn.cweb.app.data;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.util.Base64BytesAdapter;
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
public class State {
    byte[] base64;

    public State() {
    }

    public State(byte[] base64) {
        this.base64 = base64;
    }

    public byte[] getBase64() {
        return base64;
    }

    public void setBase64(byte[] base64) {
        this.base64 = base64;
    }
}
