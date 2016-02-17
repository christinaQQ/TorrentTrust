package moe.cdn.cweb.examples;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.dht.*;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;

import javax.inject.Inject;

public class WorkingExampleHashMap17 {
    static final Random RND = new Random(17);
    private static final SignedUserRecord USER17 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "17");
    private static final SignedUserRecord USER18 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "18");
    private static final SignedUserRecord USER19 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "19");

    private static final Function<Hash, BigInteger> KEY_REDUCER =
            h -> new BigInteger(SignatureUtils.sha1(h.getHashvalue().toByteArray()));
    private static final BiPredicate<Hash, SignedUserRecord> KEY_FILTER =
            (h, u) -> u.getUser().getPublicKey().getHash().equals(h);
    private static DhtNodeFactory DHT_REQUEST_FACTORY = new CwebDhtNodeFactory();

    static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        // TODO replace
        // this is an array where [0] is the master
        PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] =
                        new PeerBuilderDHT(new PeerBuilder(new Number160(RND)).ports(port).start())
                                .start();
            } else {
                peers[i] = new PeerBuilderDHT(
                        new PeerBuilder(new Number160(RND)).masterPeer(peers[0].peer()).start())
                        .start();
            }
            new IndirectReplication(peers[i]).replicationFactor(5).start();
        }
        return peers;
    }

    /**
     * Bootstraps peers to the first peer in the array.
     *
     * @param peers The peers that should be bootstrapped
     */
    static void bootstrap(PeerDHT[] peers) {
        // tell all peers about each other starting from master at 0
        // TODO replace
        Collection<PeerAddress> all =
                Arrays.stream(peers).map(PeerDHT::peerAddress).collect(Collectors.toList());
        List<FutureBootstrap> allFutures =
                Arrays.stream(peers).map(p -> p.peer().bootstrap().bootstrapTo(all).start())
                        .collect(Collectors.toList());
        net.tomp2p.futures.Futures.whenAll(allFutures).awaitUninterruptibly();
    }

    public static void main(String[] args) throws Exception {
        // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
        PeerDHT master = null;
        try {
            PeerDHT[] peers = createAndAttachPeersDHT(100, 4001);
            master = peers[0];

            bootstrap(peers);

            DhtNode<SignedUserRecord> sender1 = DHT_REQUEST_FACTORY.create(peers[0],
                    "domain", SignedUserRecord.PARSER);
            DhtNode<SignedUserRecord> sender2 = DHT_REQUEST_FACTORY.create(peers[17],
                    "domain", SignedUserRecord.PARSER);

            DhtNode<SignedUserRecord> receiver = DHT_REQUEST_FACTORY.create(
                    peers[23], "domain", SignedUserRecord.PARSER);

            CwebMap<Hash, SignedUserRecord> map1 =
                    new CwebMapImpl<>(sender1, KEY_REDUCER, KEY_FILTER);
            CwebMap<Hash, SignedUserRecord> map2 =
                    new CwebMapImpl<>(sender2, KEY_REDUCER, KEY_FILTER);

            CwebMap<Hash, SignedUserRecord> map3 =
                    new CwebMapImpl<>(receiver, KEY_REDUCER, KEY_FILTER);

            // insert some data and wait
            Futures.successfulAsList(
                    map1.put(USER17.getUser().getPublicKey().getHash(), USER17),
                    map2.put(USER18.getUser().getPublicKey().getHash(), USER18),
                    map3.put(USER19.getUser().getPublicKey().getHash(), USER19));

            Future<SignedUserRecord> one = map3.get(USER18.getUser().getPublicKey().getHash());
            System.out.println("getOne() ==> " + one.get());

            Future<Collection<SignedUserRecord>> all = map3.all(
                    USER18.getUser().getPublicKey().getHash());
            System.out.println("getAll() ==> " + all.get());

            System.out.println("Done.");
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }


}
