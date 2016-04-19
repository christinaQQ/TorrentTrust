package moe.cdn.cweb.dht.security;

import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.SignedVoteHistory;

public interface CwebSignatureValidationService extends SignatureValidationService {

    /**
     * Validates a signed vote to make sure it's authentic
     *
     * @param signedVote
     * @return indicator boolean of whether the vote is authentic
     */
    boolean validateVote(SignedVote signedVote);

    /**
     * Validates a signed user to make sure it's authentic
     *
     * @param signedUser
     * @return indicator boolean of whether the user is authentic
     */
    boolean validateUser(SignedUser signedUser);


    /**
     * Validates a signed vote history to make sure it's authentic
     *
     * @param signedVoteHistory
     * @return indicator boolean of whether the vote history object is authentic
     */
    boolean validateVoteHistory(SignedVoteHistory signedVoteHistory);
}
