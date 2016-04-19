package moe.cdn.cweb.app.data;

import moe.cdn.cweb.app.util.Base64BytesAdapter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author davix
 */
@XmlRootElement
public class State {
    @XmlJavaTypeAdapter(Base64BytesAdapter.class)
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
