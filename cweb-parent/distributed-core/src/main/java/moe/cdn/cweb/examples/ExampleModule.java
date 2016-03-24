package moe.cdn.cweb.examples;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import moe.cdn.cweb.dht.DhtModule;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.security.CwebId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * @author davix
 */
public class ExampleModule extends AbstractModule {
    private final int port;
    private final Collection<DhtPeerAddress> idAndAddresses;

    public ExampleModule(int port) {
        this(port, Collections.emptyList());
    }

    public ExampleModule(int port, Collection<DhtPeerAddress> idAndAddresses) {
        this.port = port;
        this.idAndAddresses = idAndAddresses;
    }

    public ExampleModule(int port, DhtPeerAddress... idAndAddresses) {
        this.port = port;
        this.idAndAddresses = Arrays.asList(idAndAddresses);
    }

    @Provides
    PeerEnvironment providePeerEnvironment() {
        return new PeerEnvironment() {
            @Override
            public Collection<DhtPeerAddress> getPeerAddresses() {
                return Collections.emptyList();
            }

            @Override
            public int getLocalTcpPort() {
                return port;
            }

            @Override
            public int getLocalUdpPort() {
                return port;
            }

            @Override
            public CwebId getMyId() {
                return new CwebId(new Random());
            }
        };
    }

    @Override
    protected void configure() {
        install(new DhtModule());
        bindConstant().annotatedWith(UserDomain.class).to("user");
        bindConstant().annotatedWith(VoteDomain.class).to("vote");
    }

}
