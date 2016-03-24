package moe.cdn.cweb.examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.grapher.graphviz.GraphvizGrapher;
import com.google.inject.grapher.graphviz.GraphvizModule;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.internal.ManagedPeerDhtPeer;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.Representations;
import moe.cdn.cweb.security.utils.SignatureUtils;

public class WorkingExampleHashMap17 {
    // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
    private static final Logger logger = LogManager.getLogger();

    private static final int PORT = 1717;
    // Values > 38 seem to exhaust resources
    private static final int NUM_PEERS = 30;

    private static final KeyPair USER17_KEYS = KeyUtils.generateKeyPair();
    private static final KeyPair USER18_KEYS = KeyUtils.generateKeyPair();
    private static final KeyPair USER19_KEYS = KeyUtils.generateKeyPair();

    private static final User USER17 =
            User.newBuilder().setPublicKey(USER17_KEYS.getPublicKey()).setHandle("User 17").build();
    private static final User USER18 =
            User.newBuilder().setPublicKey(USER18_KEYS.getPublicKey()).setHandle("User 18").build();
    private static final User USER19 =
            User.newBuilder().setPublicKey(USER19_KEYS.getPublicKey()).setHandle("User 19").build();

    private static final SignedUser USER17_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER17_KEYS, USER17)).setUser(USER17).build();
    private static final SignedUser USER18_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER18_KEYS, USER18)).setUser(USER18).build();
    private static final SignedUser USER19_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessage(USER19_KEYS, USER19)).setUser(USER19).build();

    static ManagedPeerDhtPeer[] createPeers(Injector[] injectors) throws IOException {
        ManagedPeerDhtPeer[] peers = new ManagedPeerDhtPeer[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            logger.debug("Creating peer " + i);
            peers[i] = injectors[i].getInstance(ManagedPeerDhtPeer.class);
            peers[i].setReplication(5);
        }
        return peers;
    }


    /**
     * Bootstraps peers to the first peer in the array.
     *
     * @param peers The peers that should be bootstrapped
     * @throws ExecutionException
     * @throws InterruptedException
     */
    static void bootstrap(ManagedPeerDhtPeer[] peers)
            throws InterruptedException, ExecutionException {
        logger.info("Bootstrapping nodes...");
        Collection<DhtPeerAddress> all = Arrays.stream(peers).map(ManagedPeerDhtPeer::getAddress)
                .collect(Collectors.toList());
        // FIXME: Can't do an async bootstrap. Causes a netty error.
        Arrays.stream(peers).forEach(peer -> peer.bootstrapToSync(all));
        logger.debug("All peers {}", all);
    }

    static void debugUserObjects() {
        logger.debug("User 17: {}", Representations.asString(USER17_SIGNED));
        logger.debug("User 18: {}", Representations.asString(USER18_SIGNED));
        logger.debug("User 19: {}", Representations.asString(USER19_SIGNED));
    }

    static void visualize(Injector injected) {
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get("out.dot")))) {
            Injector injector = Guice.createInjector(new GraphvizModule());
            GraphvizGrapher grapher = injector.getInstance(GraphvizGrapher.class);
            grapher.setOut(out);
            grapher.setRankdir("TB");
            grapher.graph(injected);
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Injector[] injectors = new Injector[NUM_PEERS];
        for (int i = 0; i < injectors.length; i++) {
            injectors[i] = Guice.createInjector(new ExampleModule(PORT + i));
        }

        // Visualize deps
        visualize(injectors[0]);

        // Create peers
        ManagedPeerDhtPeer[] peers = createPeers(injectors);

        // Bootstrap peers
        bootstrap(peers);

        Injector injector1 = injectors[0];
        Injector injector2 = injectors[17];
        Injector injector3 = injectors[23];

        CwebMultiMap<SignedUser> map1 =
                injector1.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));
        CwebMultiMap<SignedUser> map2 =
                injector2.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));
        CwebMultiMap<SignedUser> map3 =
                injector3.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));

        debugUserObjects();

        boolean r1 = map1.put(USER17_KEYS.getPublicKey().getHash(), USER17_SIGNED).get();
        boolean r2 = map2.put(USER18_KEYS.getPublicKey().getHash(), USER18_SIGNED).get();
        boolean r3 = map3.put(USER19_KEYS.getPublicKey().getHash(), USER19_SIGNED).get();
        logger.debug("Put results " + r1 + "," + r2 + "," + r3);

        // insert some data and wait
        @SuppressWarnings("unchecked")
        List<Boolean> putResults =
                Futures.allAsList(map1.put(USER17_KEYS.getPublicKey().getHash(), USER17_SIGNED),
                        map2.put(USER18_KEYS.getPublicKey().getHash(), USER18_SIGNED),
                        map3.put(USER19_KEYS.getPublicKey().getHash(), USER19_SIGNED)).get();
        if (putResults.stream().allMatch(Boolean::booleanValue)) {
            logger.info("Seeded. M1:17, M2:18, M3:19");
        } else {
            logger.error("Failed to seed.");
        }

        // Test that getting from own map is done
        waitAndPrint("M1 getOne(17) ==> %s",
                toStringFuture(map1.get(USER17_KEYS.getPublicKey().getHash())));
        waitAndPrint("M2 getOne(18) ==> %s",
                toStringFuture(map2.get(USER18_KEYS.getPublicKey().getHash())));
        waitAndPrint("M3 getOne(19) ==> %s",
                toStringFuture(map3.get(USER19_KEYS.getPublicKey().getHash())));

        // Test that we can get items that are interspersed
        waitAndPrint("M3 getOne(18) ==> %s",
                toStringFuture(map3.get(USER18_KEYS.getPublicKey().getHash())));
        waitAndPrint("M3 getAll(18) ==> %s", map3.all(USER18_KEYS.getPublicKey().getHash()));

        logger.info("Shutting down...");
        Futures.allAsList(
                Arrays.stream(peers).map(ManagedPeerDhtPeer::shutdown).collect(Collectors.toList()))
                .get();
        logger.info("Done.");
    }

    private static Future<String> toStringFuture(ListenableFuture<SignedUser> futureItem)
            throws InterruptedException, ExecutionException {
        return Futures.lazyTransform(futureItem, (Function<SignedUser, String>) u -> u == null
                ? "null" : Representations.asString(u));
    }

    private static void waitAndPrint(String format, Future<?> futureItem)
            throws InterruptedException, ExecutionException {
        System.out.println(String.format(format, futureItem.get()));
    }
}
