package moe.cdn.cweb.security;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

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
     * Adds a {@link Vote} and signs it with the current key.
     *
     * @param vote the vote
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> addSignature(Vote vote, Signature signature);

    /**
     * Imports a {@link User} and signs it with the current key. The existing record is updated
     * if it exists.
     *
     * @param user the user
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importUser(User user) throws SignatureException, InvalidKeyException;

    /**
     * Imports a {@link User} using the specified signature. The existing record is updated if it
     * exists.
     *
     * @param user      the user
     * @param signature the signature
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importSignature(User user, Signature signature);

    @Override
    default boolean importSignature(Message message, Signature signature) {
        return false;
    }
}
