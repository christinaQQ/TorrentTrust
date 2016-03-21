package moe.cdn.cweb.dht;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.security.SecurityModule;
import moe.cdn.cweb.dht.storage.StorageModule;
import moe.cdn.cweb.dht.util.Number160s;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.CwebMisc;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.Storage;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DhtModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger();

    @Provides
    @Singleton
    @UserDomain
    static Number160 provideUserDomainNumber160(@UserDomain String domainKey) {
        return Number160.createHash(domainKey);
    }

    @Provides
    @Singleton
    @VoteDomain
    static Number160 provideVoteDomainNumber160(@VoteDomain String domainKey) {
        return Number160.createHash(domainKey);
    }

    @Provides
    @Singleton
    static DhtNode<SignedUser> provideSignedUserDhtNode(DhtNodeFactory factory,
                                                        PeerDHT self,
                                                        @UserDomain String domainKey) {
        return factory.create(self, domainKey, SignedUser.PARSER);
    }

    @Provides
    @Singleton
    static DhtNode<SignedVote> provideSignedVoteDhtNode(DhtNodeFactory factory,
                                                        PeerDHT self,
                                                        @VoteDomain String domainKey) {
        return factory.create(self, domainKey, SignedVote.PARSER);
    }
//
//    @Provides
//    @Singleton
//    @Self
//    static PeerDHT provideSelfPeerDHT(Storage storage, PeerEnvironment peerEnvironment) {
//        return providePeerDHT(storage, peerEnvironment);
//    }

    @Provides
    @Singleton
    public static PeerDHT providePeerDHT(Storage storage, PeerEnvironment peerEnvironment)
            throws IOException {
        // FIXME: Do not initialize a local DHT node in a Guice module
        Peer peer = new PeerBuilder(Number160s.fromCwebId(peerEnvironment.getMyId()))
                .tcpPort(peerEnvironment.getLocalTcpPort())
                .udpPort(peerEnvironment.getLocalUdpPort()).start();
        PeerDHT peerDHT = new PeerBuilderDHT(peer).storage(storage).start();
        logger.info("Listening on {} (id = {})", peerDHT.peerAddress().peerSocketAddress(),
                peerEnvironment.getMyId());

        List<PeerAddress> peerAddresses =
                peerEnvironment.getPeerAddresses().stream()
                        .map(idAndAddress -> new PeerAddress(
                                Number160s.fromCwebId(idAndAddress.id),
                                new InetSocketAddress(idAndAddress.hostAndPort.getHostText(),
                                        idAndAddress.hostAndPort.getPort())))
                        .collect(Collectors.toList());

        logger.debug("Bootstrapping to {}", peerAddresses);
        peerDHT.peer().bootstrap().bootstrapTo(peerAddresses).start()
                .awaitListenersUninterruptibly();
        logger.debug("Done bootstrapping.");
        return peerDHT;
    }

    @Override
    protected void configure() {
        // ThrowingProviderBinder.forModule(this);

        install(new SecurityModule());
        install(new StorageModule());

        bind(DhtNodeFactory.class).to(CwebDhtNodeFactory.class).in(Singleton.class);

        // Register all protobuf types for CwebMapFactory
        bind(new TypeLiteral<CwebMapFactory<SignedUser>>() {})
                .to(new TypeLiteral<CwebMapFactoryImpl<SignedUser>>() {});
        bind(new TypeLiteral<CwebMapFactory<SignedVote>>() {})
                .to(new TypeLiteral<CwebMapFactoryImpl<SignedVote>>() {});

//        install(new FactoryModuleBuilder()
//                .implement(new TypeLiteral<CwebMap<SignedUser>>() {},
//                        new TypeLiteral<CwebMapImpl<SignedUser>>() {})
//                .build(new TypeLiteral<CwebMapFactory<SignedUser>>() {
//                }));
//        install(new FactoryModuleBuilder()
//                .implement(new TypeLiteral<CwebMap<SignedVote>>() {},
//                        new TypeLiteral<CwebMapImpl<SignedVote>>() {})
//                .build(new TypeLiteral<CwebMapFactory<SignedVote>>() {}));

        // Register all protobuf types for CwebMap
        bind(new TypeLiteral<CwebMap<SignedUser>>() {})
                .to(new TypeLiteral<CwebMapImpl<SignedUser>>() {});
        bind(new TypeLiteral<CwebMap<SignedVote>>() {})
                .to(new TypeLiteral<CwebMapImpl<SignedVote>>() {});

        bind(new TypeLiteral<Function<Hash, CwebId>>() {}).toInstance(CwebMisc.BIG_INTEGER_REDUCER);

        bind(new TypeLiteral<BiPredicate<Hash, SignedUser>>() {})
                .toInstance(CwebMisc.HASH_SIGNED_USER_BI_PREDICATE);

        bind(new TypeLiteral<BiPredicate<Hash, SignedVote>>() {})
                .toInstance(CwebMisc.HASH_SIGNED_VOTE_BI_PREDICATE);
    }
}
