package moe.cdn.cweb.security;

import com.google.common.util.concurrent.Futures;
import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.TorrentTrustProtos.VoteHistory;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.concurrent.Future;

public interface CwebImportService extends SignatureImportService {

    /**
     * Adds a {@link Vote} and signs it with the current key.
     *
     * @param vote the vote
     * @return {@code true} if the import succeeded
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    Future<Boolean> addVote(Vote vote) throws SignatureException, InvalidKeyException;

    /**
     * Imports a {@link User} and signs it with the current key. The existing
     * record is updated if it exists.
     *
     * @param user the user
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importUser(User user) throws SignatureException, InvalidKeyException;

    /**
     * Imports a public {@link User.TrustAssertion} for the current user.
     *
     * @param trustAssertion the trust assertion
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importTrustAssertion(User.TrustAssertion trustAssertion);

    /**
     * Imports a {@link Vote} record into the vote table and signs it with the
     * current key.
     *
     * @param vote the vote
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importSignature(Vote vote, Signature signature);

    /**
     * Imports a {@link User} using the specified signature. The existing record
     * is updated if it exists.
     *
     * @param user      the user
     * @param signature the signature
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importSignature(User user, Signature signature);

    /**
     * Imports a {@link VoteHistory} into the vote history table and signs it
     * with the current key.
     *
     * @param voteHistory the vote's history
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importSignature(VoteHistory voteHistory, Signature signature);

    @Override
    default Future<Boolean> importSignature(Message message, Signature signature) {
        return Futures.immediateFuture(false);
    }
}
