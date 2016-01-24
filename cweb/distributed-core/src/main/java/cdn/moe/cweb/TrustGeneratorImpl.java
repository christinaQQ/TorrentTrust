package cdn.moe.cweb;

import java.util.*;

/**
 * @author eyeung
 */
public class TrustGeneratorImpl implements TrustGenerator {

    CwebApi api;
    public TrustGeneratorImpl(CwebApi api) {
        this.api = api;
    }
    public double correlationCoefficient(User a, User b) {
        HashMap<ContentObject, Integer> overlapping_votes =
                new HashMap<ContentObject, Integer>();
        List<Vote> A_votes = api.getVotesForUser(a);
        List<Vote> B_votes = api.getVotesForUser(b);
        for (Vote v : A_votes) {
            ContentObject co = v.getContentObject();
             // temporary use
            int assertion = v.getAssertions().get(0).overallContentAssertion();
            overlapping_votes.put(co,assertion);
        }
        float positive_a = 0;
        float positive_b = 0;
        float positive_both = 0;
        for (Vote v: B_votes) {
            ContentObject co = v.getContentObject();
            int b_assertion = v.getAssertions().get(0).overallContentAssertion();
            if (b_assertion > 0) {
                positive_b += 1;
            }
            if (overlapping_votes.containsKey(co)) {
                if (overlapping_votes.get(co) > 0) {
                    positive_a +=1;
                    if (b_assertion > 0) {
                        positive_both +=1;
                    }
                }
            }
            overlapping_votes.put(co, b_assertion);
        }
        positive_a = positive_a / (float) A_votes.size();
        positive_b = positive_b / (float) B_votes.size();
        positive_both = positive_both /
                (float) overlapping_votes.keySet().size();

        double theta = (positive_both - positive_a * positive_b ) /
                Math.sqrt(positive_a * (1-positive_a) * positive_b * (1-positive_b));
        return theta;
    }

    // first iteration: Trust only users connected to A.
    public double trustCoefficientDirect(User a, User b) {
        List<User> trustedUsers = api.getTrustedUsersForUser(a);
        return trustedUsers.contains(b) ? 1 : 0;
    }

    // second iteration: Trust any user in A's connected component
    public double trustCoefficientNetwork(User a, User b) {
        return trustCoefficientNumSteps(a, b, Integer.MAX_VALUE);
    }

    // third iteration: Measure trust on number of steps
    public double trustCoefficientNumSteps(User a, User b, int num_steps) {
        Queue<User> q = new LinkedList<>();
        HashSet<User> seen = new HashSet<>();
        q.offer(a);
        q.offer(null);
        int level = 0;
        while (!q.isEmpty()) {
            User u = q.poll();
            if (u == null) {
                level +=1;
                if (level >= num_steps) {
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
