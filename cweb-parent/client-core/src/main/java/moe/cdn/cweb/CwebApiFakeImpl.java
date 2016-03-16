package moe.cdn.cweb;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

import java.util.List;
import java.util.Map;

/**
 * @author eyeung
 */
public class CwebApiFakeImpl implements CwebApi {
    Map<User, List<Vote>> userVotes;
    Map<User, List<User>> trustedGraph;
    Map<String, List<Vote>> userObjVotes;

    public CwebApiFakeImpl(Map<User, List<Vote>> userVotes,
                           Map<User, List<User>> trustedGraph,
                           Map<String, List<Vote>> userObjVotes) {
        this.userVotes = userVotes;
        this.trustedGraph = trustedGraph;
        this.userObjVotes = userObjVotes;
    }

    @Override
    public List<Vote> getVotesForUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Bad user input!");
        }
        return userVotes.get(user);
    }

    @Override
    public List<User> getTrustedUsersForUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Bad user input!");
        }
        return trustedGraph.get(user);
    }

    @Override
    public List<Vote> getVotes(Hash objectHash) {
        if (objectHash == null) {
            throw new IllegalArgumentException("Bad objecthash");
        }
        return userObjVotes.get(objectHash);
    }
}
