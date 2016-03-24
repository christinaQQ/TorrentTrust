package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.security.CwebId;

/**
 * Environment interface for providing parameters to bootstrap the DHT
 * 
 * @author davix, jim
 */
public interface PeerEnvironment {

    /**
     * Returns a collection of {@link IdAndAddress} objects that represnt peers
     * used for bootstrapping.
     * 
     * @return collection of id and address objects
     */
    Collection<IdAndAddress> getPeerAddresses();

    /**
     * Returns an integer specifying the incoming port used for TCP connections
     * to the DHT
     * 
     * @return integer in range 0-65535
     */
    int getLocalTcpPort();

    /**
     * Returns an integer specifying the incoming port used for UDP connections
     * to the DHT
     * 
     * @return integer in range 0-65535
     */
    int getLocalUdpPort();

    /**
     * Returns a {@link CwebId} that represents the identifier that the current
     * node has chosen or is configured to use. This ID determines where the
     * node is located in the DHT.
     * 
     * @return
     */
    CwebId getMyId();

    /**
     * Object to store both node identities and addresses.
     * 
     * @author davix, jim
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
