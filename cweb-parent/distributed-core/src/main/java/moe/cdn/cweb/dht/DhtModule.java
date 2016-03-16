package moe.cdn.cweb.dht;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.storage.StorageModule;
import moe.cdn.cweb.dht.util.Number160s;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.Storage;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class DhtModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger();

    @Provides
    static DhtNode<SignedUser> provideSignedUserDhtNode(DhtNodeFactory factory,
            PeerDHT self,
            @UserDomain String domainKey) {
        return factory.create(self, domainKey, SignedUser.PARSER);
    }

    @Provides
    static DhtNode<SignedVote> provideSignedVoteDhtNode(DhtNodeFactory factory,
            PeerDHT self,
            @VoteDomain String domainKey) {
        return factory.create(self, domainKey, SignedVote.PARSER);
    }

    @Provides
    @Singleton
    static PeerDHT providePeerDHT(Storage storage, PeerEnvironment peerEnvironment)
            throws IOException {

        PeerDHT peerDHT = new PeerBuilderDHT(
                new PeerBuilder(Number160s.fromCwebId(peerEnvironment.getMyId()))
                        .tcpPort(peerEnvironment.getLocalTcpPort())
                        .udpPort(peerEnvironment.getLocalUdpPort()).start()).storage(storage)
                                .start();
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
        install(new StorageModule());

        bind(DhtNodeFactory.class).to(CwebDhtNodeFactory.class);

        bind(Number160.class).annotatedWith(UserDomain.class)
                .toInstance(Number160.createHash("user"));
        bind(Number160.class).annotatedWith(VoteDomain.class)
                .toInstance(Number160.createHash("vote"));

        bind(String.class).annotatedWith(UserDomain.class).toInstance("user");
        bind(String.class).annotatedWith(VoteDomain.class).toInstance("vote");
    }
}
