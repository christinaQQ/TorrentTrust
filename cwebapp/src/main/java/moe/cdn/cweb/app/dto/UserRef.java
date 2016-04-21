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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        UserRef other = (UserRef) obj;
        if (publicKey == null) {
            if (other.publicKey != null)
                return false;
        } else if (!publicKey.equals(other.publicKey))
            return false;
        return true;
    }
}
