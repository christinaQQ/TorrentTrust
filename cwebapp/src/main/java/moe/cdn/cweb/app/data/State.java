package moe.cdn.cweb.app.data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.util.Base64BytesAdapter;
import moe.cdn.cweb.app.util.Base64PublicKeyMapAdapter;

/**
 * @author davix
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class State {
    @XmlJavaTypeAdapter(Base64PublicKeyMapAdapter.class)
    private Map<SecurityProtos.Key, HashSet<TrustedIdentity>> trustedIdentities;
    private Set<TrustAlgorithm> possibleTrustAlgorithms;
    private TrustAlgorithm currentTrustAlgorithm;
    private Set<UserIdentity> userIdentities;
    private UserIdentity currentIdentity;
    @XmlJavaTypeAdapter(Base64BytesAdapter.class)
    private Map<SecurityProtos.Key, CwebObject> objects;
}
