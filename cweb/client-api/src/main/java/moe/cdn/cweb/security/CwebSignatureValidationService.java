package moe.cdn.cweb.security;

import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;

public interface CwebSignatureValidationService extends SignatureValidationService {
    boolean validateVote(SignedVote signedVote);

    boolean validateUser(SignedUserRecord signedUser);
}
