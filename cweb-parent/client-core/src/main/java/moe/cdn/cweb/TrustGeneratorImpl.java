package moe.cdn.cweb;

import com.google.inject.Inject;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

import java.util.*;

/**
 * @author eyeung
 */

class TrustGeneratorImpl implements TrustGenerator {

    private final CwebApi api;

    @Inject
    public TrustGeneratorImpl(CwebApi api) {
        this.api = api;
    }

    private static int ratingToIntValue(Vote.Assertion.Rating rating) {
        switch (rating) {
            case BAD:
                return -1;
            case GOOD:
                return 1;
            case UNRECOGNIZED:
                return 0;
            default:
                throw new IllegalStateException("missing enum branch");
        }
    }

    @Override
    public double correlationCoefficient(User a, User b) {
        Set<VoteWrapperObject> A_votes = new HashSet<>();
        Set<VoteWrapperObject> B_votes = new HashSet<>();
        HashMap<VoteWrapperObject, VoteWrapperObject> overlappingVotes = new HashMap<>();

        try {
            for (Vote v : api.getVotesForUser(a)) {
                A_votes.add(new VoteWrapperObject(v));
            }
            for (Vote v : api.getVotesForUser(b)) {
                B_votes.add(new VoteWrapperObject(v));
            }
        } catch (CwebApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }

        Set<VoteWrapperObject> overlapping_content = new HashSet<>(A_votes);
        overlapping_content.retainAll(B_votes);
        for (VoteWrapperObject v : overlapping_content) {
            overlappingVotes.put(v, null);
        }

        // FIXME
        for (VoteWrapperObject v : B_votes) {
            if (overlappingVotes.containsKey(v)) {
                overlappingVotes.put(v, v);
            }
        }

        double positive_a = 0;
        double positive_b = 0;
        double positive_both = 0;

        double total_assertions_a = 0;
        double total_assertions_b = 0;
        double total_assertions_both = 0;

        // calculate positive a
        for (VoteWrapperObject v : A_votes) {
            for (Vote.Assertion assertion : v.vote.getAssertionList()) {
                if (assertion.getRating() == Vote.Assertion.Rating.GOOD) {
                    positive_a++;
                }
                total_assertions_a++;
            }
        }

        // calculate positive b
        for (VoteWrapperObject v : B_votes) {
            for (Vote.Assertion assertion : v.vote.getAssertionList()) {
                if (assertion.getRating() == Vote.Assertion.Rating.GOOD) {
                    positive_b++;
                }
                total_assertions_b++;
            }
        }

        // calculate positive both
        for (VoteWrapperObject v : overlappingVotes.keySet()) {
            List<Vote.Assertion> A_assertions = v.vote.getAssertionList();
            List<Vote.Assertion> B_assertions = overlappingVotes.get(v).vote.getAssertionList();
            // okay screw sets, let's do it with lists for something working and
            // make efficient
            // later
            // Set<Vote.Assertion> A_assertions = new
            // HashSet<>(A_vote.vote.getAssertionList());
            // Set<Vote.Assertion> B_assertions = new
            // HashSet<>(B_vote.vote.getAssertionList());

            for (Vote.Assertion a_assertion : A_assertions) {
                for (Vote.Assertion b_assertion : B_assertions) {
                    if (a_assertion.getContentProperty() == b_assertion.getContentProperty()) {
                        total_assertions_both++;
                        if (a_assertion.getRating() == b_assertion.getRating()
                                && a_assertion.getRating() == Vote.Assertion.Rating.GOOD) {
                            positive_both++;
                        }
                    }
                }
            }
        }


        positive_a = positive_a / total_assertions_a;
        positive_b = positive_b / total_assertions_b;
        positive_both = positive_both / total_assertions_both;

        double theta = (positive_both - positive_a * positive_b)
                / Math.sqrt(positive_a * (1 - positive_a) * positive_b * (1 - positive_b));
        return theta;
    }

    // first iteration: Trust only users connected to A.
    @Override
    public double trustCoefficientDirect(User a, User b) {
        List<User> trustedUsers;
        try {
            trustedUsers = api.getTrustedUsersForUser(a);
            return trustedUsers.contains(b) ? 1 : 0;
        } catch (CwebApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    // second iteration: Trust any user in A's connected component
    @Override
    public double trustCoefficientNetwork(User a, User b) {
        return trustCoefficientNumSteps(a, b, Integer.MAX_VALUE);
    }

    // third iteration: Measure trust on number of steps
    @Override
    public double trustCoefficientNumSteps(User a, User b, int num_steps) {
        Queue<User> q = new LinkedList<>();
        HashSet<User> seen = new HashSet<>();
        q.offer(a);
        q.offer(null);
        int level = 0;
        while (!q.isEmpty()) {
            User u = q.poll();
            if (u == null) {
                level += 1;
                if (level >= num_steps || q.isEmpty()) {
                    return 0;
                }
                q.offer(null);
                continue;
            }
            if (seen.contains(u)) {
                continue;
            }
            seen.add(u);
            if (u == b) {
                return level;
            }
            try {
                for (User n : api.getTrustedUsersForUser(u)) {
                    q.offer(n);
                }
            } catch (CwebApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

    private class VoteWrapperObject {
        Vote vote;

        private VoteWrapperObject(Vote v) {
            this.vote = v;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof VoteWrapperObject)) {
                return false;
            }

            VoteWrapperObject otherVote = (VoteWrapperObject) o;
            return (this.vote.getContentHash().equals(otherVote.vote.getContentHash()));
        }

        @Override
        public int hashCode() {
            return this.vote.getContentHash().hashCode();
        }
    }
}
