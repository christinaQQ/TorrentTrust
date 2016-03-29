package moe.cdn.cweb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.KeyEnviroment;

/**
 * Environment that stores configuration for the application.
 * 
 * @author davix, jim
 */
public class GlobalEnvironment implements PeerEnvironment, KeyEnviroment {
    private static final int DEFAULT_PORT = 1717;

    private final Collection<DhtPeerAddress> idAndAddresses;
    private final int tcpPort;
    private final int udpPort;
    private final CwebId myId;
    private final KeyPair keyPair;

    private GlobalEnvironment(Collection<DhtPeerAddress> idAndAddresses,
            int tcpPort,
            int udpPort,
            CwebId myId,
            KeyPair keyPair) {
        this.idAndAddresses = checkNotNull(idAndAddresses);
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.myId = checkNotNull(myId);
        this.keyPair = checkNotNull(keyPair);
    }

    @Override
    public Collection<DhtPeerAddress> getPeerAddresses() {
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

    @Override
    public CwebId getMyId() {
        return myId;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Builder for {@link GlobalEnviroment}
     * 
     * @author jim
     */
    public static class Builder {
        private int tcpPort;
        private int udpPort;
        private CwebId myId;
        private KeyPair keyPair;
        private final ArrayList<DhtPeerAddress> idAndAddresses;

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

        public Builder addIdAndAddress(CwebId id, HostAndPort hostAndPort) {
            idAndAddresses.add(new DhtPeerAddress(id, hostAndPort));
            return this;
        }

        public Builder addAllIdAndAddresses(Collection<DhtPeerAddress> idAndAddresses) {
            this.idAndAddresses.addAll(idAndAddresses);
            return this;
        }

        public Builder setId(CwebId myId) {
            this.myId = checkNotNull(myId);
            return this;
        }

        public Builder setKeyPair(KeyPair keyPair) {
            this.keyPair = checkNotNull(keyPair);
            return this;
        }

        public GlobalEnvironment build() {
            return new GlobalEnvironment(idAndAddresses, tcpPort, udpPort, myId, keyPair);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilderFromArgs(Iterable<String> args) {
        Builder builder = newBuilder();
        CwebId id = null;
        HostAndPort hostAndPort;
        for (String arg : args) {
            if (id == null) {
                id = CwebId.fromBase64(arg);
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

    public static Builder newBuilderFromConfigFile(File configFile) {
        try {
            Ini config = new Ini(configFile);
            // TODO actually read the config
            Builder builder = newBuilder();
            return builder;
        } catch (InvalidFileFormatException e) {
            throw new IllegalArgumentException("Provided file failed to parse", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
