package moe.cdn.cweb.dht;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @author davix
 */
public interface PeerEnvironment {
    Collection<IdAndAddress> getPeerAddresses();

    /**
     * @author davix
     */
    class IdAndAddress {
        final BigInteger id;
        final HostAndPort hostAndPort;

        public IdAndAddress(BigInteger id, HostAndPort hostAndPort) {
            Preconditions.checkArgument(id.signum() == 1, "id must be positive");
            Preconditions.checkArgument(id.bitLength() < 160, "id must be less than 160 bits long");
            this.id = id;
            this.hostAndPort = hostAndPort;
        }
    }
}
