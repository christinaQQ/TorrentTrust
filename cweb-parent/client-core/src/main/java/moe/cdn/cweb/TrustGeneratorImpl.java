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
    private Map<User, Double> centralityCache;

    @Inject
    public TrustGeneratorImpl(CwebApi api) {
        this.api = api;
        centralityCache = null;
    }

    @Override
    public double correlationCoefficient(User a, User b) throws CwebApiException {
        // build the vectors
        Map<SecurityProtos.Hash, Vote> voteVectorA = new HashMap<>();

        double score = 0;
        for (Vote v : api.getVotesForUser(a)) {
            voteVectorA.put(v.getContentHash(), v);
        }
        for (Vote v : api.getVotesForUser(b)) {
            // only handle overlapping content
            if (voteVectorA.containsKey(v.getContentHash())) {
                score += voteVectorA.get(v.getContentHash()).getAssertion(0).getRatingValue() *
                        v.getAssertion(0).getRatingValue();
            }
        }
        return score;
    }

    @Override
    public double trustCoefficient(User a, User b, TrustApi.TrustMetric trustMetric) {
        switch (trustMetric) {
            case ONLY_FRIENDS :
                return trustCoefficientDirect(a, b);
            case CONNECTED_COMPONENT:
                return trustCoefficientNetwork(a, b);
            case EIGENTRUST:
                return trustCoefficientCentrality(a, b);
            default:
                return 0.0;
        }
    }

    // first iteration: Trust only users connected to A.
    @Override
    public double trustCoefficientDirect(User a, User b) {
        Set<User> trustedUsers;
        try {
            trustedUsers = new HashSet<>(api.getTrustedUsersForUser(a));
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

    @Override
    public double trustCoefficientCentrality(User src, User tgt) {
        //arbitrarily say run 10 iterations of eigenvector centrality
        if (centralityCache != null) {
            return centralityCache.get(tgt);
        }
        return 0.0;
    }

}
