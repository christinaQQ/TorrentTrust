package moe.cdn.cweb.examples;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.CwebMapFactory;
import moe.cdn.cweb.dht.DhtModule;
import moe.cdn.cweb.dht.DhtNode;
import moe.cdn.cweb.dht.DhtNodeFactory;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.annotations.KeyLookup;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.internal.PeerDhtShutdownable;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.CwebMisc;

/**
 * @author davix
 */
public class ExampleModule extends AbstractModule {
    private final int port;
    private final Collection<PeerEnvironment.IdAndAddress> idAndAddresses;

    public ExampleModule(int port) {
        this(port, Collections.emptyList());
    }

    public ExampleModule(int port, Collection<PeerEnvironment.IdAndAddress> idAndAddresses) {
        this.port = port;
        this.idAndAddresses = idAndAddresses;
    }

    public ExampleModule(int port, PeerEnvironment.IdAndAddress... idAndAddresses) {
        this.port = port;
        this.idAndAddresses = Arrays.asList(idAndAddresses);
    }

    @Provides
    @KeyLookup
    static CwebMap<SignedUser> provideSignedUserCwebMap(CwebMapFactory<SignedUser> cwebMapFactory,
            @KeyLookup DhtNode<SignedUser> dhtNode) {
        return cwebMapFactory.create(dhtNode, CwebMisc.BIG_INTEGER_REDUCER,
                CwebMisc.HASH_SIGNED_USER_BI_PREDICATE);
    }

    @Provides
    @Singleton
    @KeyLookup
    static DhtNode<SignedUser> provideSignedUserDhtNode(DhtNodeFactory factory,
            PeerDhtShutdownable self,
            @UserDomain String domainKey) {
        return factory.create(self, domainKey, SignedUser.PARSER);
    }

    @Provides
    PeerEnvironment providePeerEnvironment() {
        return new PeerEnvironment() {
            @Override
            public Collection<IdAndAddress> getPeerAddresses() {
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
