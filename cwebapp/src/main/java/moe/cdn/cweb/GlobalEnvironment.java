package moe.cdn.cweb;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.ini4j.Ini;

import com.google.common.net.HostAndPort;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.security.CwebId;

/**
 * Environment that stores configuration for the application.
 *
 * @author davix, jim
 */
public class GlobalEnvironment implements PeerEnvironment, KeyEnvironment {
    private static final int DEFAULT_PORT = 1717;

    private final Collection<DhtPeerAddress> idAndAddresses;
    private final int tcpPort1;
    private final int udpPort1;
    private final CwebId myId;
    private final KeyEnvironment keyEnvironment;
    private final URI keyEnvironmentConfigPath;
    private final int udpPort2;
    private final int tcpPort2;

    private GlobalEnvironment(Collection<DhtPeerAddress> idAndAddresses,
            int tcpPort1,
            int udpPort1,
                              int tcpPort2,
                              int udpPort2,
            CwebId myId,
            KeyEnvironment identityEnvironment,
            URI keyEnvironmentConfigPath) {
        this.idAndAddresses = checkNotNull(idAndAddresses);
        this.tcpPort1 = tcpPort1;
        this.udpPort1 = udpPort1;
        this.tcpPort2 = tcpPort2;
        this.udpPort2 = udpPort2;
        this.myId = myId;
        this.keyEnvironment = checkNotNull(identityEnvironment);
        this.keyEnvironmentConfigPath = checkNotNull(keyEnvironmentConfigPath);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilderFromArgs(String... args) {
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

    public static Builder newBuilderFromConfigFile(File configFile) throws IOException {
        Ini config = new Ini();
        config.getConfig().setMultiSection(true);
        config.load(configFile);
        // TODO: newBuilderFromConfigFile
        
        
        Builder builder = newBuilder();
        return builder;
    }

    @Override
    public Collection<DhtPeerAddress> getPeerAddresses() {
        return idAndAddresses;
    }

    @Override
    public int getLocalTcpPort1() {
        return tcpPort1;
    }

    @Override
    public int getLocalUdpPort1() {
        return udpPort1;
    }

    @Override
    public int getLocalTcpPort2() {
        return tcpPort2;
    }

    @Override
    public int getLocalUdpPort2() {
        return udpPort2;
    }

    @Override
    public CwebId getMyId() {
        return myId;
    }

    @Override
    public KeyPair getKeyPair() {
        return keyEnvironment.getKeyPair();
    }

    @Override
    public Iterator<KeyPair> iterator() {
        return keyEnvironment.iterator();
    }

    public URI getKeyEnvironmentConfigPath() {
        return keyEnvironmentConfigPath;
    }

    /**
     * Builder for {@link GlobalEnvironment}.
     *
     * @author jim
     */
    public static class Builder {
        private final ArrayList<DhtPeerAddress> idAndAddresses;
        private int tcpPort1;
        private int udpPort1;
        private CwebId myId;
        private KeyEnvironment keyEnvironment;
        private URI keyEnvironmentConfigPath;
        private int tcpPort2;
        private int udpPort2;

        public Builder() {
            idAndAddresses = new ArrayList<>();
        }

        public Builder setTcpPort1(int port) {
            tcpPort1 = port;
            return this;
        }

        public Builder setUdpPort1(int port) {
            udpPort1 = port;
            return this;
        }      
        
        public Builder setTcpPort2(int port) {
            tcpPort2 = port;
            return this;
        }

        public Builder setUdpPort2(int port) {
            udpPort2 = port;
            return this;
        }

        public Builder setPort1(int port) {
            setTcpPort1(port);
            setUdpPort1(port);
            return this;
        }
        
        public Builder setPort2(int port) {
            setTcpPort2(port);
            setUdpPort2(port);
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

        public Builder setKeyEnvironment(KeyEnvironment keyEnvironment) {
            this.keyEnvironment = checkNotNull(keyEnvironment);
            return this;
        }

        public Builder setKeyEnvironmentConfigPath(URI keyEnvironmentConfigPath) {
            this.keyEnvironmentConfigPath = checkNotNull(keyEnvironmentConfigPath);
            return this;
        }

        public GlobalEnvironment build() {
            return new GlobalEnvironment(idAndAddresses, tcpPort1, udpPort1, tcpPort2, udpPort2,
                    myId, keyEnvironment, keyEnvironmentConfigPath);
        }
    }
}
