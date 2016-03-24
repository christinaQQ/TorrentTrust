package moe.cdn.cweb.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.internal.PeerDhtShutdownable;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;

public class WorkingExampleHashMap17 {
    // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
    private static final int PORT = 1717;

    private static final KeyPair USER17_KEYS = KeyUtils.generateKeyPair();
    private static final KeyPair USER18_KEYS = KeyUtils.generateKeyPair();
    private static final KeyPair USER19_KEYS = KeyUtils.generateKeyPair();

    private static final User USER17 =
            User.newBuilder().setPublicKey(USER17_KEYS.getPublicKey()).setHandle("User 17").build();
    private static final User USER18 =
            User.newBuilder().setPublicKey(USER18_KEYS.getPublicKey()).setHandle("User 17").build();
    private static final User USER19 =
            User.newBuilder().setPublicKey(USER19_KEYS.getPublicKey()).setHandle("User 17").build();

    private static final SignedUser USER17_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER17_KEYS, USER17)).setUser(USER17).build();
    private static final SignedUser USER18_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER18_KEYS, USER18)).setUser(USER18).build();
    private static final SignedUser USER19_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER19_KEYS, USER19)).setUser(USER19).build();

    static PeerDhtShutdownable[] createPeers(Injector[] injectors) throws IOException {
        PeerDhtShutdownable[] peers = new PeerDhtShutdownable[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            peers[i] = injectors[i].getInstance(PeerDhtShutdownable.class);
            new IndirectReplication(peers[i].getDhtInstance()).replicationFactor(5).start();
        }
        return peers;
        // Originally, peer[0] was the master peer and all others connected to
        // it
        // peers[i] = new PeerBuilderDHT(new
        // PeerBuilder(Number160s.fromCwebId(RND))
        // .masterPeer(peers[0].peer()).start()).start();
    }


    /**
     * Bootstraps peers to the first peer in the array.
     *
     * @param peers The peers that should be bootstrapped
     */
    static void bootstrap(PeerDhtShutdownable[] peers) {
        // tell all peers about each other starting from master at 0
        // TODO replace
        Collection<PeerAddress> all = Arrays.stream(peers).map(PeerDhtShutdownable::getDhtInstance)
                .map(PeerDHT::peerAddress).collect(Collectors.toList());
        List<FutureBootstrap> allFutures = Arrays.stream(peers)
                .map(p -> p.getDhtInstance().peer().bootstrap().bootstrapTo(all).start())
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


        // PeerDHT master = null;
        try {
            PeerDhtShutdownable[] peers = createPeers(injectors);
            // master = peers[0];

            bootstrap(peers);

            Injector injector1 = injectors[0];
            Injector injector2 = injectors[17];
            Injector injector3 = injectors[23];

            CwebMap<SignedUser> map1 =
                    injector1.getInstance(Key.get(new TypeLiteral<CwebMap<SignedUser>>() {}));
            CwebMap<SignedUser> map2 =
                    injector2.getInstance(Key.get(new TypeLiteral<CwebMap<SignedUser>>() {}));
            CwebMap<SignedUser> map3 =
                    injector3.getInstance(Key.get(new TypeLiteral<CwebMap<SignedUser>>() {}));


            boolean r1 = map1.put(USER17_KEYS.getPublicKey().getHash(), USER17_SIGNED).get();
            boolean r2 = map2.put(USER18_KEYS.getPublicKey().getHash(), USER18_SIGNED).get();
            boolean r3 = map3.put(USER19_KEYS.getPublicKey().getHash(), USER19_SIGNED).get();
            System.out.println("Put results " + r1 + "," + r2 + "," + r3);


            // insert some data and wait
            @SuppressWarnings("unchecked")
            List<Boolean> putResults =
                    Futures.allAsList(map1.put(USER17_KEYS.getPublicKey().getHash(), USER17_SIGNED),
                            map2.put(USER18_KEYS.getPublicKey().getHash(), USER18_SIGNED),
                            map3.put(USER19_KEYS.getPublicKey().getHash(), USER19_SIGNED)).get();
            if (putResults.stream().allMatch(Boolean::booleanValue)) {
                System.out.println("Seeded.");
            } else {
                System.out.println("Failed to seed.");
            }

            Future<SignedUser> one = map3.get(USER18_KEYS.getPublicKey().getHash());
            System.out.println("getOne() ==> " + one.get());

            Future<Collection<SignedUser>> all = map3.all(USER18_KEYS.getPublicKey().getHash());
            System.out.println("getAll() ==> " + all.get());

            System.out.println("Shutting down...");
            Futures.allAsList(Arrays.stream(peers).map(PeerDhtShutdownable::shutdown)
                    .collect(Collectors.toList())).get();
            System.out.println("Done.");

        } finally {
        }
    }


}
