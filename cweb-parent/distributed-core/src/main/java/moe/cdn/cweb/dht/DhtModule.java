package moe.cdn.cweb.dht;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.annotations.MyPeerId;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.storage.StorageModule;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.Storage;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    static PeerDHT providePeerDHT(@MyPeerId Number160 id,
            Storage storage,
            PeerEnvironment peerEnvironment) throws IOException {
        PeerDHT peerDHT =
                new PeerBuilderDHT(new PeerBuilder(id).tcpPort(peerEnvironment.getLocalTcpPort())
                        .udpPort(peerEnvironment.getLocalUdpPort()).start()).storage(storage)
                                .start();

        List<PeerAddress> peerAddresses =
                peerEnvironment.getPeerAddresses().stream().map(idAndAddress -> {
                    if (idAndAddress.id.bitLength() > Number160.BITS) {
                        throw new IllegalArgumentException(
                                "ID should be at most 160 bits: " + idAndAddress.id);
                    }
                    return new PeerAddress(new Number160(idAndAddress.id.toByteArray()),
                            new InetSocketAddress(idAndAddress.hostAndPort.getHostText(),
                                    idAndAddress.hostAndPort.getPort()));
                }).collect(Collectors.toList());

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

        // TODO: determine own peer ID
        bind(Number160.class).annotatedWith(MyPeerId.class).toInstance(new Number160(new Random()));
    }
}
