package moe.cdn.cweb.examples;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.CwebMap;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class WorkingExampleHashMap17 {
    // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
    private static final int PORT = 1717;
    private static final TorrentTrustProtos.SignedUser USER17 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "17");
    private static final TorrentTrustProtos.SignedUser USER18 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "18");
    private static final TorrentTrustProtos.SignedUser USER19 =
            SignatureUtils.buildSignedUserRecord(SignatureUtils.generateKeypair(), "19");

    static PeerDHT[] createPeers(Injector[] injectors) throws IOException {
        PeerDHT[] peers = new PeerDHT[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            peers[i] = injectors[i].getInstance(PeerDHT.class);
            new IndirectReplication(peers[i]).replicationFactor(5).start();
        }
        return peers;
        // Originally, peer[0] was the master peer and all others connected to it
        //    peers[i] = new PeerBuilderDHT(new PeerBuilder(Number160s.fromCwebId(RND))
        //            .masterPeer(peers[0].peer()).start()).start();
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
        Injector[] injectors = new Injector[100];
        for (int i = 0; i < injectors.length; i++) {
            injectors[i] = Guice.createInjector(new ExampleModule(PORT));
        }

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get("out.dot")))) {
            Injector injector = Guice.createInjector(new GraphvizModule());
            GraphvizGrapher grapher = injector.getInstance(GraphvizGrapher.class);
            grapher.setOut(out);
            grapher.setRankdir("TB");
            grapher.graph(injectors[0]);
        }


//        PeerDHT master = null;
        try {
            PeerDHT[] peers = createPeers(injectors);
//            master = peers[0];

            bootstrap(peers);

            Injector injector1 = injectors[0];
            Injector injector2 = injectors[17];
            Injector injector3 = injectors[23];

            CwebMap<SignedUser> map1 = injector1.getInstance(Key.get(
                    new TypeLiteral<CwebMap<SignedUser>>() {
                    }));
            CwebMap<SignedUser> map2 = injector2.getInstance(Key.get(
                    new TypeLiteral<CwebMap<SignedUser>>() {
                    }));
            CwebMap<SignedUser> map3 = injector3.getInstance(Key.get(
                    new TypeLiteral<CwebMap<SignedUser>>() {
                    }));

            // insert some data and wait
            Futures.successfulAsList(
                    map1.put(USER17.getUser().getPublicKey().getHash(), USER17),
                    map2.put(USER18.getUser().getPublicKey().getHash(), USER18),
                    map3.put(USER19.getUser().getPublicKey().getHash(), USER19));

            Future<SignedUser> one = map3.get(USER18.getUser().getPublicKey().getHash());
            System.out.println("getOne() ==> " + one.get());

            Future<Collection<SignedUser>> all =
                    map3.all(USER18.getUser().getPublicKey().getHash());
            System.out.println("getAll() ==> " + all.get());

            System.out.println("Done.");
        } finally {
//            if (master != null) {
//                master.shutdown();
//            }
        }
    }


}
