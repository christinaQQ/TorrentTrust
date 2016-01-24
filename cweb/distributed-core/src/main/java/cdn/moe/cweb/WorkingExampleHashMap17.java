package cdn.moe.cweb;

import com.google.protobuf.ByteString;
import net.tomp2p.dht.FutureDigest;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.Futures;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;
import net.tomp2p.utils.Utils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WorkingExampleHashMap17 {

    static final Random RND = new Random(17);

    static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        // TODO replace
        // this is an array where [0] is the master
        PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] = new PeerBuilderDHT(
                        new PeerBuilder(new Number160(RND)).ports(port).start())
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
        Collection<PeerAddress> all = Arrays.stream(peers).map(PeerDHT::peerAddress)
                .collect(Collectors.toList());
        List<FutureBootstrap> allFutures = Arrays.stream(peers)
                .map(p -> p.peer().bootstrap().bootstrapTo(all).start())
                .collect(Collectors.toList());
        Futures.whenAll(allFutures).awaitUninterruptibly();
    }

    public static void main(String[] args) throws Exception {
        PeerDHT master = null;
        try {
            PeerDHT[] peers = createAndAttachPeersDHT(100, 4001);
            master = peers[0];

            bootstrap(peers);

            CwebCollection<TorrentTrustProtos.User> sender1 = new CwebCollection<>(peers[0],
                    Number160.createHash("location"), Number160.createHash("domain"), TorrentTrustProtos.User.PARSER);
            CwebCollection<TorrentTrustProtos.User> sender2 = new CwebCollection<>(peers[17],
                    Number160.createHash("location"), Number160.createHash("domain"), TorrentTrustProtos.User.PARSER);

            CwebCollection<TorrentTrustProtos.User> receiver = new CwebCollection<>(peers[23],
                    Number160.createHash("location"), Number160.createHash("domain"), TorrentTrustProtos.User.PARSER);

            sender1.add(TorrentTrustProtos.User.newBuilder()
                    .setPublicKey(ByteString.copyFromUtf8("17")).build()).awaitUninterruptibly();
            sender1.add(TorrentTrustProtos.User.newBuilder()
                    .setPublicKey(ByteString.copyFromUtf8("18")).build()).awaitUninterruptibly();

            sender2.add(TorrentTrustProtos.User.newBuilder()
                    .setPublicKey(ByteString.copyFromUtf8("19")).build()).awaitUninterruptibly();

            CwebFutureGet<TorrentTrustProtos.User> futureGet = receiver.get();
            futureGet.awaitUninterruptibly();
            Map<Number640, Data> map = futureGet.dataMap();
//            for (Data data : map.values()) {
//                @SuppressWarnings("unchecked")
//                String received = (String) data.object();
//                System.out.println("Received: " + received);
//            }
            futureGet.rawData().forEach((pa, m) -> {
                System.out.println("Peer found: " + pa);
            });
            futureGet.all().forEach(d -> {
                System.out.println("received: " + d);
            });

            FutureDigest futureDigest = peers[23].digest(Utils.makeSHAHash(Utils.encodeJavaObject("17"))
            ).start();
            futureDigest.awaitUninterruptibly();
            System.out.println("we found the data on:");
            for(Map.Entry<PeerAddress, DigestResult> entry: futureDigest.rawDigest().entrySet()) {
                System.out.println("peer " + entry.getKey()+" reported " +entry.getValue().keyDigest().size());
            }
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }

}