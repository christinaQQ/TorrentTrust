package moe.cdn.cweb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;

import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

/**
 * @author eyeung
 */
public class TrustGeneratorImpl implements TrustGenerator {

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
        HashMap<ByteString, Integer> overlapping_votes = new HashMap<>();
        List<Vote> A_votes = api.getVotesForUser(a);
        List<Vote> B_votes = api.getVotesForUser(b);
        for (Vote v : A_votes) {
            ByteString co = v.getContentHash().getHashvalue();
            // FIXME(eyeung): change assertion weighting when vote assertion
            // interface is finalized
            int assertion = ratingToIntValue(v.getAssertion(0).getRating());
            overlapping_votes.put(co, assertion);
        }
        double positive_a = 0;
        double positive_b = 0;
        double positive_both = 0;
        for (Vote v : B_votes) {
            ByteString co = v.getContentHash().getHashvalue();
            // FIXME(eyeung)
            int b_assertion = ratingToIntValue(v.getAssertion(0).getRating());
            if (b_assertion > 0) {
                positive_b += 1;
            }
            if (overlapping_votes.containsKey(co)) {
                if (overlapping_votes.get(co) > 0) {
                    positive_a += 1;
                    if (b_assertion > 0) {
                        positive_both += 1;
                    }
                }
            }
            overlapping_votes.put(co, b_assertion);
        }
        positive_a = positive_a / (double) A_votes.size();
        positive_b = positive_b / (double) B_votes.size();
        positive_both = positive_both / (double) overlapping_votes.keySet().size();

        double theta = (positive_both - positive_a * positive_b)
                / Math.sqrt(positive_a * (1 - positive_a) * positive_b * (1 - positive_b));
        return theta;
    }

    // first iteration: Trust only users connected to A.
    @Override
    public double trustCoefficientDirect(User a, User b) {
        List<User> trustedUsers = api.getTrustedUsersForUser(a);
        return trustedUsers.contains(b) ? 1 : 0;
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
            for (User n : api.getTrustedUsersForUser(u)) {
                q.offer(n);
            }
        }
        return 0;
    }
}
