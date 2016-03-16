package moe.cdn.cweb.security;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

public interface CwebImportService extends SignatureImportService {
    /**
     * Imports a vote on an object and signs it with the current user's key
     *
     * @param vote
     * @return
     */
    boolean importVote(Vote vote);

    /**
     * Imports a new user into the trust network updating the existing record if
     * it exists. Signs with the current user's key.
     *
     * @param user
     * @return
     */
    boolean importUser(User user);

    boolean importSignature(Vote vote, Signature signature);

    boolean importSignature(User vote, Signature signature);

    @Override
    default boolean importSignature(Message message, Signature signature) {
        return false;
    }
}
