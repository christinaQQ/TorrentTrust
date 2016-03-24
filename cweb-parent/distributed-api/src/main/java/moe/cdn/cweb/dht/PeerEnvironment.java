package moe.cdn.cweb.dht;

import java.util.Collection;

import moe.cdn.cweb.security.CwebId;

/**
 * Environment interface for providing parameters to bootstrap the DHT
 * 
 * @author davix, jim
 */
public interface PeerEnvironment {

    /**
     * Returns a collection of {@link DhtPeerAddress} objects that represnt peers
     * used for bootstrapping.
     * 
     * @return collection of id and address objects
     */
    Collection<DhtPeerAddress> getPeerAddresses();

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
}
