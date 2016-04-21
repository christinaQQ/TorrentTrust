package moe.cdn.cweb;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.inject.Inject;

import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

/**
 * @author eyeung
 */

class TrustGeneratorImpl implements TrustGenerator {

    private final CwebApi api;

    @Inject
    public TrustGeneratorImpl(CwebApi api) {
        this.api = api;
    }

    private double transform(double value) {
        if (value == 0) {
            return -1;
        }
        return value;
    }

    @Override
    public double correlationCoefficient(User a, User b) throws CwebApiException {
        // build the vectors
        Map<SecurityProtos.Hash, Vote> voteVectorA = new HashMap<>();
        List<Vote> aVotes = api.getVotesForUser(a);
        List<Vote> bVotes = api.getVotesForUser(b);
        if (aVotes == null || bVotes == null) {
            return 0.0;
        }

        double score = 0;
        for (Vote v : aVotes) {
            voteVectorA.put(v.getContentHash(), v);
        }
        for (Vote v : bVotes) {
            // only handle overlapping content
            if (voteVectorA.containsKey(v.getContentHash())) {
                score += transform(voteVectorA.get(v.getContentHash()).getAssertion(0).getRatingValue()) *
                        transform(v.getAssertion(0).getRatingValue());
            }
        }
        return score;
    }

    @Override
    public double trustCoefficient(User a, User b, TrustApi.TrustMetric trustMetric) {
        switch (trustMetric) {
            case ONLY_FRIENDS :
                return trustCoefficientDirect(a, b);
            case FRIENDS_OF_FRIENDS:
                return trustCoefficientNumSteps(a, b, 2);
            case CONNECTED_COMPONENT:
                return trustCoefficientNetwork(a, b);
            case EIGENTRUST:
                return trustCoefficientCentrality(a, b);
            default:
                throw new IllegalStateException("Invalid trust metric");
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
    // this should probably be cached
    @Override
    public double trustCoefficientNetwork(User a, User b) {
        Set<User> reachable = bfs(a, b);
        return (reachable.contains(b)) ? 1.0 : 0.0;
    }


    /**
     *
     * @param a
     * @param b
     * @return the set of users reachable from a bfs from a
     */
    private Set<User> bfs(User a, User b) {
        Queue<User> q = new ArrayDeque<>();
        HashSet<User> seen = new HashSet<>();
        q.offer(a);
        while (!q.isEmpty()) {
            User u = q.poll();
            if (seen.contains(u)) {
                continue;
            }
            seen.add(u);
            try {
                for (User n : api.getTrustedUsersForUser(u)) {
                    q.offer(n);
                }
            } catch (CwebApiException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return seen;
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
                if (level > num_steps || q.isEmpty()) {
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
                return 1.0;
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
        //arbitrarily say run 17 iterations of eigenvector centrality
        Set<User> reachable = bfs(src, tgt);
        Map<User, Double> v = new HashMap<User, Double>();
        Map<User, Double> v_t = new HashMap<User, Double>();

        if (!reachable.contains(tgt)) {
            return 0.0;
        }

        for (User u : reachable) {
            v.put(u, 1.0 / reachable.size());
            v_t.put(u, 0.0);
        }
        
        for (int i = 0; i < 17 ; i++) {
            for (User u : reachable) {
                try {
                    List<User> trusted = api.getTrustedUsersForUser(u);
                    for (User neighbor : trusted) {
                        double new_value = v_t.get(neighbor) +
                                v.get(u) / api.getTrustedUsersForUser(neighbor).size();
                        v_t.put(neighbor, new_value);
                    }
                } catch (CwebApiException e) {
                    e.printStackTrace();
                }
            }
            v = v_t;
            v_t = new HashMap<User, Double>();
            for (User u : reachable) {
                v_t.put(u, 0.0);
            }
        }
        return v.get(tgt);
    }

}
