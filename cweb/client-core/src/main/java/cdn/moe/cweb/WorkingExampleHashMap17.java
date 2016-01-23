package cdn.moe.cweb;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.Map;
import java.util.Random;


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
        for (PeerDHT from : peers) {
            for (PeerDHT to : peers) {
                from.peerBean().peerMap().peerFound(to.peerAddress(), null, null, null);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PeerDHT master = null;
        try {
            PeerDHT[] peers = createAndAttachPeersDHT(100, 4001);
            master = peers[0];

            bootstrap(peers);

            PeerCollectionAccessor<String> sender1 = new PeerCollectionAccessor<>(peers[0],
                    Number160.createHash("location"), Number160.createHash("domain"));
            PeerCollectionAccessor<String> sender2 = new PeerCollectionAccessor<>(peers[17],
                    Number160.createHash("location"), Number160.createHash("domain"));

            PeerCollectionAccessor receiver = new PeerCollectionAccessor(peers[23],
                    Number160.createHash("location"), Number160.createHash("domain"));

            sender1.add("17").awaitUninterruptibly();
            sender1.add("18").awaitUninterruptibly();

            sender2.add("19").awaitUninterruptibly();

            FutureGet futureGet = receiver.get();
            futureGet.awaitUninterruptibly();
            Map<Number640, Data> map = futureGet.dataMap();
            for (Data data : map.values()) {
                @SuppressWarnings("unchecked")
                String received = (String) data.object();
                System.out.println("Received: " + received);
            }
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }

}