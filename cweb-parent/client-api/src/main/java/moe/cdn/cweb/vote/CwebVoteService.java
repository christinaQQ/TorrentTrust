package moe.cdn.cweb.vote;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ListenableFuture;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

public interface CwebVoteService {
    /**
     * Gets a list of all votes for a certain object hash. If no object
     * indicated by the hash exists, a future of an empty collection will be
     * returned.
     *
     * @param object
     * @return future of a collection of votes.
     */
    ListenableFuture<Collection<Vote>> getAllVotes(Hash object);

    /**
     * Cast a vote for a certain object.
     *
     * @param vote
     * @return future of boolean indicator indicating whether the vote was
     *         successfully cast
     */
    Future<Boolean> castVote(Vote vote) throws SignatureException, InvalidKeyException;
}
