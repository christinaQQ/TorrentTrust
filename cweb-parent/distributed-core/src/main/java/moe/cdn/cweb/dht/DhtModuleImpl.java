package moe.cdn.cweb.dht;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.inject.Singleton;

import com.google.inject.Key;
import moe.cdn.cweb.dht.spi.DhtModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.annotations.DhtNodeController;
import moe.cdn.cweb.dht.annotations.KeyLookup;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.internal.ManagedPeerDhtPeer;
import moe.cdn.cweb.dht.security.DhtSecurityModule;
import moe.cdn.cweb.dht.storage.StorageModule;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.CwebMisc;
import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number160;

public class DhtModuleImpl extends DhtModule {
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
    static ManagedDhtNode<SignedUser> provideSignedUserDhtNode(DhtNodeFactory factory,
            ManagedPeerDhtPeer self,
            @UserDomain String domainKey) {
        return factory.create(self, domainKey, SignedUser.PARSER);
    }

    @Provides
    @Singleton
    static ManagedDhtNode<SignedVote> provideSignedVoteDhtNode(DhtNodeFactory factory,
            ManagedPeerDhtPeer self,
            @VoteDomain String domainKey) {
        return factory.create(self, domainKey, SignedVote.PARSER);
    }

    // FIXME: Should CwebMaps be injected at a different level?
    @Provides
    @Singleton
    @UserDomain
    static CwebMultiMap<SignedUser> provideHashSignedUserCwebMap(
            CwebMapFactory<SignedUser> cwebMapFactory, ManagedDhtNode<SignedUser> dhtNodeUser) {
        return cwebMapFactory.create(dhtNodeUser, CwebMisc.CWEB_ID_REDUCER,
                CwebMisc.HASH_SIGNED_USER_BI_PREDICATE);
    }

    @Provides
    @Singleton
    @VoteDomain
    static CwebMultiMap<SignedVote> provideHashSignedVoteCwebMap(
            CwebMapFactory<SignedVote> cwebMapFactory, ManagedDhtNode<SignedVote> dhtNodeVote) {
        return cwebMapFactory.create(dhtNodeVote, CwebMisc.CWEB_ID_REDUCER,
                CwebMisc.HASH_SIGNED_VOTE_BI_PREDICATE);
    }

    @Provides
    @Singleton
    @KeyLookup
    static CwebMultiMap<SignedUser> provideKeyLookupCwebMap(
            CwebMapFactory<SignedUser> cwebMapFactory,
            ManagedDhtNode<SignedUser> dhtNodeUser) {
        return cwebMapFactory.create(dhtNodeUser, CwebMisc.CWEB_ID_REDUCER,
                CwebMisc.HASH_SIGNED_USER_BI_PREDICATE);
    }

    @Provides
    @Singleton
    public static ManagedPeerDhtPeer provideManagedPeerDhtPeer(Storage storage,
                                                               PeerEnvironment peerEnvironment)
            throws IOException {
        // FIXME: Do not initialize a local DHT node in a Guice module

        ManagedPeerDhtPeer peerDhtPeer =
                ManagedPeerDhtPeer.fromEnviroment(peerEnvironment, storage);
        logger.info("Local peer listening on {}", peerDhtPeer.getAddress());

        logger.debug("Bootstrapping to {}", peerEnvironment.getPeerAddresses());
        try {
            peerDhtPeer.bootstrapTo(peerEnvironment.getPeerAddresses()).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error(e);
        }
        logger.debug("Done bootstrapping.");

        return peerDhtPeer;
    }

    @Override
    protected void configure() {
        // ThrowingProviderBinder.forModule(this);
        requireBinding(Key.get(String.class, UserDomain.class));
        requireBinding(Key.get(String.class, VoteDomain.class));
        requireBinding(PeerEnvironment.class);

        install(new DhtSecurityModule());
        install(new StorageModule());

        bind(DhtNodeFactory.class).to(CwebDhtNodeFactory.class).in(Singleton.class);
        bind(ManagedPeer.class).annotatedWith(DhtNodeController.class).to(ManagedPeerDhtPeer.class)
                .in(Singleton.class);

        // Register all protobuf types for CwebMapFactory
        bind(new TypeLiteral<CwebMapFactory<SignedUser>>() {})
                .to(new TypeLiteral<CwebMapFactoryImpl<SignedUser>>() {});
        bind(new TypeLiteral<CwebMapFactory<SignedVote>>() {})
                .to(new TypeLiteral<CwebMapFactoryImpl<SignedVote>>() {});

        // Register all protobuf types for CwebMultiMap
        bind(new TypeLiteral<CwebMultiMap<SignedUser>>() {})
                .to(new TypeLiteral<CwebMultiMapImpl<SignedUser>>() {});
        bind(new TypeLiteral<CwebMultiMap<SignedVote>>() {})
                .to(new TypeLiteral<CwebMultiMapImpl<SignedVote>>() {});

        bind(new TypeLiteral<Function<Hash, CwebId>>() {}).toInstance(CwebMisc.CWEB_ID_REDUCER);

        bind(new TypeLiteral<BiPredicate<Hash, SignedUser>>() {})
                .toInstance(CwebMisc.HASH_SIGNED_USER_BI_PREDICATE);

        bind(new TypeLiteral<BiPredicate<Hash, SignedVote>>() {})
                .toInstance(CwebMisc.HASH_SIGNED_VOTE_BI_PREDICATE);
    }
}