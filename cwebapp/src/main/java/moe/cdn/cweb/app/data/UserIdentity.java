package moe.cdn.cweb.app.data;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.util.Base64KeyAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserIdentity {
    private String name;
    @XmlJavaTypeAdapter(Base64KeyAdapter.class)
    private SecurityProtos.Key publicKey;
    @XmlJavaTypeAdapter(Base64KeyAdapter.class)
    private SecurityProtos.Key privateKey;
}
