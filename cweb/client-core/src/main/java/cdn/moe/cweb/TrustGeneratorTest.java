package cdn.moe.cweb;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author eyeung
 */
public class TrustGeneratorTest {
    private class ContentObjectFakeImpl implements ContentObject {
        private String hash;
        private ContentObjectFakeImpl(String hash) {
            this.hash = hash;
        }
        @Override
        public String getObjectHash() {
            return this.hash;
        }
    }

    private class UserFakeImpl implements User {
        private String id;

        private UserFakeImpl(String id) {
            this.id = id;
        }
        @Override
        public String getUserId() {
            return id;
        }
    }
    User a, b, c, d;
    TrustGenerator trustgen;
    Map<User, List<Vote>> userVotes;

    @org.junit.Before
    public void setUp() throws Exception {
        Assertion goodAssertion = new AssertionFakeImpl(1);
        Assertion badAssertion = new AssertionFakeImpl(-1);
        ContentObject obj = new ContentObjectFakeImpl("abc");

        List<Assertion> assertions = new ArrayList<>();
        assertions.add(goodAssertion);
        Vote good_vote = new VoteImpl(assertions, obj);

        Map<User, List<User>> userGraph = new HashMap<>();
        a = new UserFakeImpl("a");
        b = new UserFakeImpl("b");
        c = new UserFakeImpl("c");
        d = new UserFakeImpl("d");
        userGraph.put(a, Arrays.asList(b));
        userGraph.put(b, Arrays.asList(a, c));
        userGraph.put(d, Collections.emptyList());
        userGraph.put(c, Collections.emptyList());

        userVotes = new HashMap<>();
        Map<String, List<User>> userObjVotes = new HashMap<>();

        CwebApi api = new CwebApiFakeImpl(userVotes, userGraph,
                userObjVotes);
        trustgen = new TrustGeneratorImpl(api);


    }

    @org.junit.Test
    public void testCorrelationCoefficient() throws Exception {

    }

    @org.junit.Test
    public void testTrustCoefficientDirect() {
        assertEquals("simple connection", 1,
                trustgen.trustCoefficientDirect(a,b), .001);
        assertEquals("not direct connection", 0,
                trustgen.trustCoefficientDirect(a, c), .001);
        assertEquals("no connection", 0,
                trustgen.trustCoefficientDirect(a, d), .001);
        assertEquals("only one direction", 0,
                trustgen.trustCoefficientDirect(c, b), .001);
        assertEquals("trust goes one direction", 1,
                trustgen.trustCoefficientDirect(b, c), .001);
    }

    @org.junit.Test
    public void testTrustCoefficientNetwork() {
        trustgen.trustCoefficientNetwork(a, b);
        assertEquals("simple connection", 1,
                trustgen.trustCoefficientNetwork(a, b), .001);
        assertEquals("not direct connection network", 2.0,
                trustgen.trustCoefficientNetwork(a, c), .001);

        assertEquals("no connection", 0,
                trustgen.trustCoefficientNetwork(a, d), .001);
    }

    @org.junit.Test
    public void testTrustCoefficientNumSteps() {
        assertEquals("indirect connection", 2,
                trustgen.trustCoefficientNumSteps(a, c, 3), .001);
        assertEquals("indirect connect not enough steps", 0,
                trustgen.trustCoefficientNumSteps(a, c, 1), .001);
    }

    @org.junit.Test
    public void testPerfectVoteCorrelation() {
        Assertion goodAssertion = new AssertionFakeImpl(1);
        Assertion badAssertion = new AssertionFakeImpl(-1);
        ContentObject o1 = new ContentObjectFakeImpl("ian");
        ContentObject o2 = new ContentObjectFakeImpl("davix");
        ContentObject o3 = new ContentObjectFakeImpl("fifi");

        Vote vote_o1 = new VoteImpl(Arrays.asList(goodAssertion), o1);
        Vote vote_o2 = new VoteImpl(Arrays.asList(badAssertion), o2);
        Vote vote_o3 = new VoteImpl(Arrays.asList(badAssertion), o3);

        userVotes.put(a, Arrays.asList(vote_o1, vote_o2, vote_o3));
        userVotes.put(b, Arrays.asList(vote_o1, vote_o2, vote_o3));
        double theta = trustgen.correlationCoefficient(a, b);
        System.out.println("theta = " + theta);
        assertEquals("very close correlation", 1.0,
                trustgen.correlationCoefficient(a, b), .001);
    }

    @org.junit.Test
    public void testOppositeVoteCorrelation() {
        Assertion goodAssertion = new AssertionFakeImpl(1);
        Assertion badAssertion = new AssertionFakeImpl(-1);
        ContentObject o1 = new ContentObjectFakeImpl("ian");
        ContentObject o2 = new ContentObjectFakeImpl("davix");
        ContentObject o3 = new ContentObjectFakeImpl("fifi");

        Vote vote_o1 = new VoteImpl(Arrays.asList(goodAssertion), o1);
        Vote vote_o2 = new VoteImpl(Arrays.asList(goodAssertion), o2);
        Vote vote_o3 = new VoteImpl(Arrays.asList(badAssertion), o3);

        Vote vote_o1_rev = new VoteImpl(Arrays.asList(badAssertion), o1);
        Vote vote_o2_rev = new VoteImpl(Arrays.asList(badAssertion), o2);
        Vote vote_o3_rev = new VoteImpl(Arrays.asList(goodAssertion), o3);

        userVotes.put(a, Arrays.asList(vote_o1, vote_o2, vote_o3));
        userVotes.put(b, Arrays.asList(vote_o1_rev, vote_o2_rev, vote_o3_rev));
        double theta = trustgen.correlationCoefficient(a, b);
        System.out.println("theta = " + theta);
        assertEquals("very close correlation", -1.0,
                trustgen.correlationCoefficient(a, b), .001);
    }

    @org.junit.Test
    public void testSomeCorrelation() {
        Assertion goodAssertion = new AssertionFakeImpl(1);
        Assertion badAssertion = new AssertionFakeImpl(-1);
        ContentObject o1 = new ContentObjectFakeImpl("ian");
        ContentObject o2 = new ContentObjectFakeImpl("davix");
        ContentObject o3 = new ContentObjectFakeImpl("fifi");

        Vote vote_o1 = new VoteImpl(Arrays.asList(goodAssertion), o1);
        Vote vote_o2 = new VoteImpl(Arrays.asList(goodAssertion), o2);
        Vote vote_o3 = new VoteImpl(Arrays.asList(badAssertion), o3);

        Vote vote_o1_rev = new VoteImpl(Arrays.asList(goodAssertion), o1);
        Vote vote_o2_rev = new VoteImpl(Arrays.asList(badAssertion), o2);
        Vote vote_o3_rev = new VoteImpl(Arrays.asList(badAssertion), o3);

        userVotes.put(a, Arrays.asList(vote_o1, vote_o2, vote_o3));
        userVotes.put(b, Arrays.asList(vote_o1_rev, vote_o2_rev, vote_o3_rev));
        double theta = trustgen.correlationCoefficient(a, b);
        System.out.println("theta = " + theta);
        assertEquals("some correlation", 0.5,
                trustgen.correlationCoefficient(a, b), .001);
    }
}

