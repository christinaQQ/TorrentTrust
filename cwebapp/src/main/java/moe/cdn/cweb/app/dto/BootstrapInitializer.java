package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import moe.cdn.cweb.app.util.Base64CwebIdAdapter;
import moe.cdn.cweb.security.CwebId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BootstrapInitializer {

    private String ip;

    @XmlJavaTypeAdapter(Base64CwebIdAdapter.class)
    private CwebId id;

    private int port;

    public BootstrapInitializer() {}

    public BootstrapInitializer(String ip, CwebId id, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public CwebId getId() {
        return id;
    }

    public void setId(CwebId id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
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
        BootstrapInitializer other = (BootstrapInitializer) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

}
