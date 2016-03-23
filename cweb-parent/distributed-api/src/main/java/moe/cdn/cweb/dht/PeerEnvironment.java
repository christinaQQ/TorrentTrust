package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.security.CwebId;

/**
 * @author davix
 */
public interface PeerEnvironment {

    Collection<IdAndAddress> getPeerAddresses();

    int getLocalTcpPort();

    int getLocalUdpPort();

    CwebId getMyId();

    /**
     * @author davix
     */
    class IdAndAddress {
        final CwebId id;
        final HostAndPort hostAndPort;

        public IdAndAddress(CwebId id, HostAndPort hostAndPort) {
            this.id = id;
            this.hostAndPort = hostAndPort;
        }
    }
}
