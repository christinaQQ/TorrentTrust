package moe.cdn.cweb;

import com.google.common.net.HostAndPort;
import moe.cdn.cweb.dht.PeerEnvironment;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author davix
 */
public class GlobalEnvironment implements PeerEnvironment {
    private static final int DEFAULT_PORT = 1717;

    private final Collection<IdAndAddress> idAndAddresses;

    public GlobalEnvironment(Iterable<String> args) {
        idAndAddresses = new ArrayList<>();
        BigInteger id = null;
        HostAndPort hostAndPort;
        for (String arg : args) {
            if (id == null) {
                id = new BigInteger(arg);
            } else {
                hostAndPort = HostAndPort.fromString(arg).withDefaultPort(DEFAULT_PORT);
                idAndAddresses.add(new IdAndAddress(id, hostAndPort));
                id = null;
            }
        }
        if (id != null) {
            throw new IllegalArgumentException(
                    "Expected an even number of arguments consisting of pairs of ID and host/port");
        }
    }

    @Override
    public Collection<IdAndAddress> getPeerAddresses() {
        return idAndAddresses;
    }

}
