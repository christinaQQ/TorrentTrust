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
    private final int tcpPort;
    private final int udpPort;

    private GlobalEnvironment(Collection<IdAndAddress> idAndAddresses, int tcpPort, int udpPort) {
        this.idAndAddresses = idAndAddresses;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    @Override
    public Collection<IdAndAddress> getPeerAddresses() {
        return idAndAddresses;
    }

    @Override
    public int getLocalTcpPort() {
        return tcpPort;
    }

    @Override
    public int getLocalUdpPort() {
        return udpPort;
    }

    public static class Builder {
        private int tcpPort;
        private int udpPort;
        private final ArrayList<IdAndAddress> idAndAddresses;

        public Builder() {
            idAndAddresses = new ArrayList<>();
        }

        public Builder setTcpPort(int port) {
            tcpPort = port;
            return this;
        }

        public Builder setUdpPort(int port) {
            udpPort = port;
            return this;
        }

        public Builder setPort(int port) {
            setTcpPort(port);
            setUdpPort(port);
            return this;
        }

        public Builder addIdAndAddress(BigInteger id, HostAndPort hostAndPort) {
            idAndAddresses.add(new IdAndAddress(id, hostAndPort));
            return this;
        }

        public Builder addAllIdAndAddresses(Collection<IdAndAddress> idAndAddresses) {
            this.idAndAddresses.addAll(idAndAddresses);
            return this;
        }

        public GlobalEnvironment build() {
            return new GlobalEnvironment(idAndAddresses, tcpPort, udpPort);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilderFromArgs(Iterable<String> args) {
        Builder builder = newBuilder();
        BigInteger id = null;
        HostAndPort hostAndPort;
        for (String arg : args) {
            if (id == null) {
                id = new BigInteger(arg);
            } else {
                hostAndPort = HostAndPort.fromString(arg).withDefaultPort(DEFAULT_PORT);
                builder.addIdAndAddress(id, hostAndPort);
                id = null;
            }
        }
        if (id != null) {
            throw new IllegalArgumentException(
                    "Expected an even number of arguments consisting of pairs of ID and host/port");
        }
        return builder;
    }
}
