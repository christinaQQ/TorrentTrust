package moe.cdn.cweb.dht;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.security.CwebId;

/**
 * Object to store both node identities and addresses.
 *
 * @author davix, jim
 */
public class DhtPeerAddress {
    private final CwebId id;
    private final HostAndPort hostAndPort;

    public DhtPeerAddress(CwebId id, HostAndPort hostAndPort) {
        this.id = id;
        this.hostAndPort = hostAndPort;
    }

    public CwebId getId() {
        return id;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    @Override
    public String toString() {
        return String.format("[%s] (ID: %s)", hostAndPort, id);
    }
}
