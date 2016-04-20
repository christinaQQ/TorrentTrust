package moe.cdn.cweb;

import com.google.protobuf.ByteString;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Key.KeyType;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.vote.VoteUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author eyeung
 */
public class TrustGeneratorTest {

    private static final User a =
            User.newBuilder().setPublicKey(generateFakePublicKey("a")).build();
    private static final User b =
            User.newBuilder().setPublicKey(generateFakePublicKey("b")).build();
    private static final User c =
            User.newBuilder().setPublicKey(generateFakePublicKey("c")).build();
    private static final User d =
            User.newBuilder().setPublicKey(generateFakePublicKey("d")).build();

    TrustGenerator trustGenerator;
    Map<User, List<Vote>> userVotes;
    Vote.Assertion goodAssertion =
            Vote.Assertion.newBuilder().setRating(Vote.Assertion.Rating.GOOD).build();
    Vote.Assertion badAssertion =
            Vote.Assertion.newBuilder().setRating(Vote.Assertion.Rating.BAD).build();

    private static Vote makeVote(String contentHash, List<Vote.Assertion> assertions) {
        return VoteUtils.createVote(contentHash, a.getPublicKey()).addAllAssertion(assertions)
                .build();
    }

    private static Key generateFakePublicKey(String id) {
        return Key.newBuilder().setType(KeyType.PUBLIC)
                .setHash(Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA_256)
                        .setHashValue(ByteString.EMPTY).build())
                .setRaw(ByteString.copyFromUtf8(id)).build();
    }

    @Before
    public void setUp() throws Exception {
        Map<User, List<User>> userGraph = new HashMap<>();
        userGraph.put(a, Collections.singletonList(b));
        userGraph.put(b, Arrays.asList(a, c));
        userGraph.put(d, Collections.emptyList());
        userGraph.put(c, Collections.emptyList());

        userVotes = new HashMap<>();
        Map<String, List<Vote>> userObjVotes = new HashMap<>();

        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph, userObjVotes);
        trustGenerator = new TrustGeneratorImpl(api);
    }

    @Test
    public void testCorrelationCoefficient() throws Exception {

    }

    @Test
    public void testTrustCoefficientDirect() {
        assertEquals("simple connection", 1, trustGenerator.trustCoefficientDirect(a, b), .001);
        assertEquals("not direct connection", 0, trustGenerator.trustCoefficientDirect(a, c), .001);
        assertEquals("no connection", 0, trustGenerator.trustCoefficientDirect(a, d), .001);
        assertEquals("only one direction", 0, trustGenerator.trustCoefficientDirect(c, b), .001);
        assertEquals("trust goes one direction", 1, trustGenerator.trustCoefficientDirect(b, c),
                .001);
    }

    @Test
    public void testTrustCoefficientNetwork() {
        trustGenerator.trustCoefficientNetwork(a, b);
        assertEquals("simple connection", 1, trustGenerator.trustCoefficientNetwork(a, b), .001);
        assertEquals("not direct connection network", 1.0,
                trustGenerator.trustCoefficientNetwork(a, c), .001);

        assertEquals("no connection", 0, trustGenerator.trustCoefficientNetwork(a, d), .001);
    }

    @Test
    public void testTrustCoefficientNumSteps() {
        assertEquals("indirect connection", 2, trustGenerator.trustCoefficientNumSteps(a, c, 3),
                .001);
        assertEquals("indirect connect not enough steps", 0,
                trustGenerator.trustCoefficientNumSteps(a, c, 1), .001);
        assertEquals("no connection num steps", 0, trustGenerator.trustCoefficientNumSteps(a, d, 5),
                .001);
    }

    @Test
    public void testFriendsofFriends() {
        Map<User, List<User>> userGraph = new HashMap<>();
        userGraph.put(a, Arrays.asList(b, c, d));
        userGraph.put(b, Arrays.asList(a, c, d));
        userGraph.put(d, Arrays.asList(a, b, c));
        userGraph.put(c, Arrays.asList(a, b, d));
        userVotes = new HashMap<>();
        Map<String, List<Vote>> userObjVotes = new HashMap<>();

        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph, userObjVotes);
        trustGenerator = new TrustGeneratorImpl(api);

        for (User u : userGraph.keySet()) {
            double eigentrust = trustGenerator.trustCoefficientCentrality(a, u);
            System.out.println( " eigentrust = " + eigentrust);

        }

    }

}

