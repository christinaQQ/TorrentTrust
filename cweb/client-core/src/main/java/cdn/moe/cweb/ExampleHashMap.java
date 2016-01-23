package cdn.moe.cweb;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;


public class ExampleHashMap {

    static final Random RND = new Random(17);

    static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        // TODO replace
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
        for (int i = 0; i < peers.length; i++) {
            for (int j = 0; j < peers.length; j++) {
                peers[i].peerBean().peerMap().peerFound(peers[j].peerAddress(), null, null, null);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        PeerDHT master = null;
        try {
            PeerDHT[] peers = createAndAttachPeersDHT(100, 4001);
            master = peers[0];
            MyPeer myPeer1 = new MyPeer(peers[0]);
            bootstrap(peers);
            myPeer1.put("This is my location key", "This is my domain", "This is my content key",
                    "And here comes the data").awaitUninterruptibly();
            MyPeer myPeer2 = new MyPeer(peers[5]);
            FutureGet futureGet = myPeer2.get("This is my location key", "This is my domain",
                    "This is my content key");
            futureGet.awaitUninterruptibly();
            Map<Number640, Data> map = futureGet.dataMap();
            for (Data data : map.values()) {
                @SuppressWarnings("unchecked")
                MyData<String> myData = (MyData<String>) data.object();
                System.out.println(
                        "key: " + myData.key() + ", domain: " + myData.domain() + ", content: "
                                + myData.content() + ", data: " + myData.data());
            }
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }

    private static class MyPeer {
        final private PeerDHT peer;

        private MyPeer(PeerDHT peer) {
            this.peer = peer;
        }

        private FutureGet get(String key, String domain, String content) {
            Number160 locationKey = Number160.createHash(key);
            Number160 domainKey = Number160.createHash(domain);
            Number160 contentKey = Number160.createHash(content);
            return peer.get(locationKey).domainKey(domainKey).contentKey(contentKey).start();
        }

        private FuturePut put(String key, String domain, String content, String data) throws
                IOException {
            Number160 locationKey = Number160.createHash(key);
            Number160 domainKey = Number160.createHash(domain);
            Number160 contentKey = Number160.createHash(content);
            MyData<String> myData = new MyData<String>().key(key).domain(domain).content(content)
                    .data(data);
            return peer.put(locationKey).domainKey(domainKey).data(contentKey, new Data(myData))
                    .start();
        }
    }

    private static class MyData<K> implements Serializable {
        private static final long serialVersionUID = 2098774660703812030L;

        private K key;

        private K domain;

        private K content;

        private K data;

        public K key() {
            return key;
        }

        public MyData<K> key(K key) {
            this.key = key;
            return this;
        }

        public Object domain() {
            return domain;
        }

        public MyData<K> domain(K domain) {
            this.domain = domain;
            return this;
        }

        public K content() {
            return content;
        }

        public MyData<K> content(K content) {
            this.content = content;
            return this;
        }

        public K data() {
            return data;
        }

        public MyData<K> data(K data) {
            this.data = data;
            return this;
        }
    }
}