package moe.cdn.cweb.security;

import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;

public interface CwebSignatureValidationService extends SignatureValidationService {
    
    /**
     * Validates a signed vote to make sure it's authentic
     * @param signedVote
     * @return indicator boolean of whether the vote is authentic
     */
    boolean validateVote(SignedVote signedVote);

    /**
     * Validates a signed user to make sure it's authentic
     * @param signedUser
     * @return indicator boolean of whether the user is authentic
     */
    boolean validateUser(SignedUserRecord signedUser);
}
