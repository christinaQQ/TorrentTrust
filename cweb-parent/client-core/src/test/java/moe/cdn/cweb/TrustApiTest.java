package moe.cdn.cweb;

import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.dht.security.KeyLookupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Key.KeyType;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.vote.VoteUtils;

/**
 * @author eyeung
 */
public class TrustApiTest {

    private static final User a =
            User.newBuilder().setPublicKey(Key.newBuilder().setHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("a")))).build();
    private static final User b =
            User.newBuilder().setPublicKey(Key.newBuilder().setHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("b")))).build();
    private static final User c =
            User.newBuilder().setPublicKey(Key.newBuilder().setHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("c")))).build();
    private static final User d =
            User.newBuilder().setPublicKey(Key.newBuilder().setHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("d")))).build();

    private static final TorrentTrustProtos.SignedUser a_signed =
            TorrentTrustProtos.SignedUser.newBuilder().setUser(a).build();
    private static final TorrentTrustProtos.SignedUser b_signed =
            TorrentTrustProtos.SignedUser.newBuilder().setUser(b).build();
    private static final TorrentTrustProtos.SignedUser c_signed =
            TorrentTrustProtos.SignedUser.newBuilder().setUser(c).build();
    private static final TorrentTrustProtos.SignedUser d_signed =
            TorrentTrustProtos.SignedUser.newBuilder().setUser(d).build();


    Map<User, List<User>> userGraph;
    TrustGenerator trustGenerator;
    Map<User, List<Vote>> userVotes;
    TrustApi trustApi;
    Vote.Assertion goodAssertion =
            Vote.Assertion.newBuilder().setRating(Vote.Assertion.Rating.GOOD).build();
    Vote.Assertion badAssertion =
            Vote.Assertion.newBuilder().setRating(Vote.Assertion.Rating.BAD).build();
    SecurityProtos.Hash object = SecurityProtos.Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1")).build();
    SecurityProtos.Hash object2 = SecurityProtos.Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object2")).build();

//    private static Vote makeVote(String contentHash, List<Vote.Assertion> assertions, User u) {
//        return VoteUtils.createVote(contentHash, u.getPublicKey()).addAllAssertion(assertions)
//                .build();
//    }

    private static Key generateFakePublicKey(String id) {
        return Key.newBuilder().setType(KeyType.PUBLIC)
                .setHash(Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA_256)
                        .setHashValue(ByteString.EMPTY).build())
                .setRaw(ByteString.copyFromUtf8(id)).build();
    }

    @Before
    public void setUp() throws Exception {
        userGraph = new HashMap<>();
        userGraph.put(a, Collections.singletonList(b));
        userGraph.put(b, Arrays.asList(a, c));
        userGraph.put(d, Collections.emptyList());
        userGraph.put(c, Collections.emptyList());


    }

    @Test
    public void testTrustCorrelated() throws CwebApiException, ExecutionException, InterruptedException {
        userVotes = new HashMap<>();
        Map<Hash, List<Vote>> userObjVotes = new HashMap<>();

        // user 1 votes good on object 1
        // user 2 votes good on object 1
        Vote aVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(a.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();
//        Vote aVote = makeVote(object.getHashValue().toString(), Arrays.asList(goodAssertion), a);
//        Vote bVote = makeVote(object.getHashValue().toString(), Arrays.asList(goodAssertion), b);
        Vote bVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(b.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();


        // user 2 also votes good on object 2
//        Vote bVote2 = makeVote(object2.getHashValue().toString(), Arrays.asList(goodAssertion), b);
        Vote bVote2 = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(b.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object2"))).build();

        userVotes.put(a, Arrays.asList(aVote));
        userVotes.put(b, Arrays.asList(bVote, bVote2));

        userObjVotes.put(object, Arrays.asList(aVote, bVote));
        userObjVotes.put(object2, Arrays.asList(bVote));


        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph, userObjVotes);
        trustGenerator = new TrustGeneratorImpl(api);
        KeyLookupService keyLookupService = new FakeKeyLookupServiceImpl(Arrays.asList(a_signed, b_signed, c_signed, d_signed));

        trustApi = new TrustApiImpl(api, keyLookupService, trustGenerator);

        double trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.ONLY_FRIENDS);
        System.out.println("trust = " + trust);
        assertEquals(1.0, trust, .001); // we will trust this object completely

        trust = trustApi.trustForObject(c, goodAssertion, object2, TrustApi.TrustMetric.ONLY_FRIENDS);
        assertEquals(0.0, trust, .001); //c is outside our network
    }

    @Test
    public void testTrustCorrelatedFriendsofFriends() throws CwebApiException, ExecutionException, InterruptedException {

        userVotes = new HashMap<>();
        Map<Hash, List<Vote>> userObjVotes = new HashMap<>();

        // user 1 votes good on object 1
        // user 3 votes good on object 1
        Vote aVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(a.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();
        Vote bVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(c.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();


        // user 3 also votes good on object 2
//        Vote bVote2 = makeVote(object2.getHashValue().toString(), Arrays.asList(goodAssertion), b);
        Vote bVote2 = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(c.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object2"))).build();

        userVotes.put(a, Arrays.asList(aVote));
        userVotes.put(c, Arrays.asList(bVote, bVote2));

        userObjVotes.put(object, Arrays.asList(aVote, bVote));
        userObjVotes.put(object2, Arrays.asList(bVote));


        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph, userObjVotes);
        trustGenerator = new TrustGeneratorImpl(api);
        KeyLookupService keyLookupService = new FakeKeyLookupServiceImpl(Arrays.asList(a_signed, b_signed, c_signed, d_signed));

        trustApi = new TrustApiImpl(api, keyLookupService, trustGenerator);

        double trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.ONLY_FRIENDS);
        assertEquals(0.0, trust, .001); //c is outside our network

        trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.FRIENDS_OF_FRIENDS);
        assertEquals(1.0, trust, .001); // now we trust them

        trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.CONNECTED_COMPONENT);
        assertEquals(1.0, trust, .001); // now we trust them
    }

    @Test
    public void testNegCorrelation() throws CwebApiException, ExecutionException, InterruptedException {
        userVotes = new HashMap<>();
        Map<Hash, List<Vote>> userObjVotes = new HashMap<>();

        // user 1 votes good on object 1
        // user 3 votes good on object 1
        Vote aVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(a.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();
        Vote bVote = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(c.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object1"))).build();


        // user 3 also votes good on object 2
//        Vote bVote2 = makeVote(object2.getHashValue().toString(), Arrays.asList(goodAssertion), b);
        Vote bVote2 = Vote.newBuilder().addAssertion(goodAssertion).setOwnerPublicKey(c.getPublicKey()).setContentHash(Hash.newBuilder().setHashValue(ByteString.copyFromUtf8("object2"))).build();

        userVotes.put(a, Arrays.asList(aVote));
        userVotes.put(c, Arrays.asList(bVote, bVote2));

        userObjVotes.put(object, Arrays.asList(aVote, bVote));
        userObjVotes.put(object2, Arrays.asList(bVote));


        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph, userObjVotes);
        trustGenerator = new TrustGeneratorImpl(api);
        KeyLookupService keyLookupService = new FakeKeyLookupServiceImpl(Arrays.asList(a_signed, b_signed, c_signed, d_signed));

        trustApi = new TrustApiImpl(api, keyLookupService, trustGenerator);

        double trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.ONLY_FRIENDS);
        assertEquals(0.0, trust, .001); //c is outside our network

        trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.FRIENDS_OF_FRIENDS);
        assertEquals(1.0, trust, .001); // now we trust them

        trust = trustApi.trustForObject(a, goodAssertion, object2, TrustApi.TrustMetric.CONNECTED_COMPONENT);
        assertEquals(1.0, trust, .001); // now we trust them
    }

    class FakeKeyLookupServiceImpl implements KeyLookupService {

        private  final Logger logger = LogManager.getLogger();
        private final Map<SecurityProtos.Hash, TorrentTrustProtos.SignedUser> keyserver;

        public FakeKeyLookupServiceImpl() {
            this.keyserver = new HashMap<>();
        }

        public FakeKeyLookupServiceImpl(List<TorrentTrustProtos.SignedUser> records) {
            this.keyserver = new HashMap<>(records.size());
            for (TorrentTrustProtos.SignedUser record : records) {
                keyserver.put(record.getUser().getPublicKey().getHash(), record);
            }
        }

        private void warnDeprecation() {
            logger.warn("Deprecated: FakeKeyLookupService is deprecated.");
        }

        @Override
        public ListenableFuture<Optional<TorrentTrustProtos.SignedUser>> findOwner(SecurityProtos.Key publicKey) {
            warnDeprecation();
            return Futures.immediateFuture(Optional.ofNullable(keyserver.get(publicKey.getHash())));
        }

        @Override
        public ListenableFuture<Optional<SecurityProtos.Key>> findKey(SecurityProtos.Hash keyHash) {
            warnDeprecation();
            if (!keyserver.containsKey(keyHash)) {
                return Futures.immediateFuture(Optional.empty());
            } else {
                return Futures
                        .immediateFuture(Optional.of(keyserver.get(keyHash).getUser().getPublicKey()));
            }
        }
    }


}

