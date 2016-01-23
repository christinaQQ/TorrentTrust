package cdn.moe.cweb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eyeung
 */
public class CwebApiFakeImpl implements CwebApi {
    Map<User, List<Vote>> userVotes;
    Map<User, List<User>> trustedGraph;
    Map<String, List<User>> userObjVotes;

    public CwebApiFakeImpl(Map<User, List<Vote>>userVotes,
            Map<User, List<User>> trustedGraph,
                           Map<String, List<User>> userObjVotes) {
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
    public List<User> getVotedUsersForObject(String objectHash) {
        if (objectHash == null) {
            throw new IllegalArgumentException("Bad objecthash");
        }
        return userObjVotes.get(objectHash);
    }

    @Override
    public List<Vote> getVotesForUserForObject(String objectHash, User user) {
        return null;
    }
}
