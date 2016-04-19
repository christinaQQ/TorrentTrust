package moe.cdn.cweb.app.data;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.util.Base64BytesAdapter;
import moe.cdn.cweb.app.util.Base64KeyAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Map;

/**
 * @author davix
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class State {
    Map<SecurityProtos.Key, TrustedUser> trustedIdentities;

}
