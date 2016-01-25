package moe.cdn.cweb;

import java.util.List;


/**
 * @author davix
 */
public interface CwebApi {

    // some of these should be changed to sets.
    List<TorrentTrustProtos.Vote> getVotesForUser(TorrentTrustProtos.User user);

    List<TorrentTrustProtos.User> getTrustedUsersForUser(TorrentTrustProtos.User user);

    List<TorrentTrustProtos.User> getVotedUsersForObject(String objectHash);

    List<TorrentTrustProtos.Vote> getVotesForUserForObject(String objectHash, TorrentTrustProtos.User user);

}
