package moe.cdn.cweb;

import java.util.List;

import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.TorrentTrustProtos.User;


/**
 * @author davix
 */
public interface CwebApi {

    // some of these should be changed to sets.
    List<Vote> getVotesForUser(User user);

    List<User> getTrustedUsersForUser(User user);

    List<User> getVotedUsersForObject(String objectHash);

    List<Vote> getVotesForUserForObject(String objectHash, User user);

}
