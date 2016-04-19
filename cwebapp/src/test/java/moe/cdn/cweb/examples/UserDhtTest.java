package moe.cdn.cweb.examples;

import com.google.common.util.concurrent.Futures;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.DhtModuleService;
import moe.cdn.cweb.dht.DhtPeerAddress;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.annotations.DhtNodeController;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class UserDhtTest {
    // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
    private static final Logger logger = LogManager.getLogger();

    // Values > 38 seem to exhaust resources
    private static final int NUM_PEERS = 30;
    private static final Key<CwebMultiMap<SignedUser>> MULTI_MAP_SIGNED_USER_KEY =
            Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {});

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
            .setSignature(SignatureUtils.signMessageUnchecked(USER17_KEYS, USER17)).setUser(USER17)
            .build();
    private static final SignedUser USER18_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessageUnchecked(USER18_KEYS, USER18)).setUser(USER18)
            .build();
    private static final SignedUser USER19_SIGNED = SignedUser.newBuilder()
            .setSignature(SignatureUtils.signMessageUnchecked(USER19_KEYS, USER19)).setUser(USER19)
            .build();

    @DataPoints
    public static Injector[] injectors;

    private static ManagedPeer[] peers;
    private static CwebMultiMap<SignedUser> map17;
    private static CwebMultiMap<SignedUser> map18;
    private static CwebMultiMap<SignedUser> map19;

    static ManagedPeer[] createPeers(Injector[] injectors) throws IOException {
        ManagedPeer[] peers = new ManagedPeer[injectors.length];
        for (int i = 0; i < injectors.length; i++) {
            logger.debug("Creating peer " + i);
            peers[i] =
                    injectors[i].getInstance(Key.get(ManagedPeer.class, DhtNodeController.class));
            peers[i].setReplication(5);
        }
        return peers;
    }

    static void bootstrap(ManagedPeer[] peers) throws InterruptedException, ExecutionException {
        logger.info("Bootstrapping nodes...");
        Collection<DhtPeerAddress> all =
                Arrays.stream(peers).map(ManagedPeer::getAddress).collect(Collectors.toList());
        // FIXME: Can't do an async bootstrap. Causes a netty error.
        Arrays.stream(peers).forEach(peer -> peer.bootstrapToSync(all));
        logger.debug("All peers {}", all);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("Initializing nodes...");
        injectors = new Injector[NUM_PEERS];
        for (int i = 0; i < injectors.length; i++) {
            injectors[i] = Guice.createInjector(DhtModuleService.getInstance().getDhtModule(),
                    new ExampleModule(1700 + i));
        }
        // Create peers
        peers = createPeers(injectors);

        // Bootstrap peers
        bootstrap(peers);

        Injector injector1 = injectors[0];
        Injector injector2 = injectors[17];
        Injector injector3 = injectors[23];

        map17 = injector1.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));
        map18 = injector2.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));
        map19 = injector3.getInstance(Key.get(new TypeLiteral<CwebMultiMap<SignedUser>>() {}));

        assertTrue(map17.put(USER17_KEYS.getPublicKey().getHash(), USER17_SIGNED).get());
        assertTrue(map18.put(USER18_KEYS.getPublicKey().getHash(), USER18_SIGNED).get());
        assertTrue(map19.put(USER19_KEYS.getPublicKey().getHash(), USER19_SIGNED).get());
        logger.info("Seeded. M1:17, M2:18, M3:19");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("Shutting down...");
        Futures.allAsList(
                Arrays.stream(peers).map(ManagedPeer::shutdown).collect(Collectors.toList())).get();
    }

    @Test
    public void testGetUserFromOwnMap() throws Exception {
        assertEquals(USER17_SIGNED, map17.get(USER17_KEYS.getPublicKey().getHash()).get());
        assertEquals(USER18_SIGNED, map18.get(USER18_KEYS.getPublicKey().getHash()).get());
        assertEquals(USER19_SIGNED, map19.get(USER19_KEYS.getPublicKey().getHash()).get());
    }

    @Test
    public void testGetAllUsersFromOwnMap() throws Exception {
        assertThat(map17.all(USER17_KEYS.getPublicKey().getHash()).get())
                .containsExactly(USER17_SIGNED);
    }

    @Test
    public void testGetUserDifferentMap() throws Exception {
        assertEquals(USER18_SIGNED, map19.get(USER18_KEYS.getPublicKey().getHash()).get());
    }

    @Test
    public void testGetAllUsersFromDifferentMap() throws Exception {
        assertThat(map19.all(USER18_KEYS.getPublicKey().getHash()).get())
                .containsExactly(USER18_SIGNED);
    }

    @Theory
    public void testAllNodesGetUser17(Injector injector) throws Exception {
        CwebMultiMap<SignedUser> userMap = injector.getInstance(MULTI_MAP_SIGNED_USER_KEY);
        SignedUser actual = userMap.get(USER17.getPublicKey().getHash()).get();
        assertEquals(USER17_SIGNED, actual);
    }

    @Theory
    public void testAllNodesGetUser18(Injector injector) throws Exception {
        CwebMultiMap<SignedUser> userMap = injector.getInstance(MULTI_MAP_SIGNED_USER_KEY);
        SignedUser actual = userMap.get(USER18.getPublicKey().getHash()).get();
        assertEquals(USER18_SIGNED, actual);
    }

    @Theory
    public void testAllNodesGetUser19(Injector injector) throws Exception {
        CwebMultiMap<SignedUser> userMap = injector.getInstance(MULTI_MAP_SIGNED_USER_KEY);
        SignedUser actual = userMap.get(USER19.getPublicKey().getHash()).get();
        assertEquals(USER19_SIGNED, actual);
    }
}
