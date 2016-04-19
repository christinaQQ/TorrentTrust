package moe.cdn.cweb;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

import java.util.List;


/**
 * @author davix
 */
public interface CwebApi {

    // fixme: some of these should be changed to sets.

    /**
     * Gets a user's voting history
     *
     * @param user
     * @return
     */
    List<Vote> getVotesForUser(User user) throws CwebApiException;

    /**
     * Gets the users in a user's local trust neighborhood
     *
     * @param user
     * @return
     */
    List<User> getTrustedUsersForUser(User user) throws CwebApiException;

    /**
     * Gets all votes for an object
     *
     * @param objectHash
     * @return
     */
    List<Vote> getVotes(Hash objectHash) throws CwebApiException;
}
