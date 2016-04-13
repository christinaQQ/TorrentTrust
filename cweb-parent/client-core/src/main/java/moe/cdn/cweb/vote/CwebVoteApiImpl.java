package moe.cdn.cweb.vote;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.SignedVoteHistory;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.annotations.VoteHistoryDomain;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;
import moe.cdn.cweb.security.CwebImportService;

class CwebVoteApiImpl implements CwebVoteApi {

    private final CwebSignatureValidationService signatureValidationService;
    private final CwebImportService importService;
    private final CwebMultiMap<SignedVote> voteMap;
    private final CwebMultiMap<SignedVoteHistory> voteHistoryMap;

    @Inject
    public CwebVoteApiImpl(CwebSignatureValidationService signatureValidationService,
                           CwebImportService importService,
                           @VoteDomain CwebMultiMap<SignedVote> voteMap,
                           @VoteHistoryDomain CwebMultiMap<SignedVoteHistory> voteHistoryMap) {
        this.signatureValidationService = checkNotNull(signatureValidationService);
        this.importService = checkNotNull(importService);
        this.voteMap = checkNotNull(voteMap);
        this.voteHistoryMap = checkNotNull(voteHistoryMap);
    }

    @Override
    public ListenableFuture<Collection<Vote>> getAllVotes(Hash objectHash) {
        return Futures.transform(voteMap.all(objectHash),
                (Function<Collection<SignedVote>, List<Vote>>) signedVotes -> signedVotes.stream()
                        .filter(signatureValidationService::validateVote).map(SignedVote::getVote)
                        .collect(Collectors.toList()));
    }

    /**
     * Gets the votes that a user has cast on an object.
     * 
     * @param objectHash object the vote is cast on
     * @param user user casting the vote
     * @return Future of vote, {@code null} if none exist
     */
    public ListenableFuture<Vote> getUserVote(Hash objectHash, User user) {
        return Futures.transform(getAllVotes(objectHash),
                (Function<Collection<Vote>, Vote>) votes -> {
                    Collection<Vote> filteredVotes =
                            votes.stream()
                                    .filter(vote -> vote.getOwnerPublicKey()
                                            .equals(user.getPublicKey()))
                            .collect(Collectors.toList());
                    return filteredVotes.isEmpty() ? null : Iterables.getOnlyElement(filteredVotes);
                });
    }

    @Override
    public ListenableFuture<Collection<Vote>> getVoteHistory(User user) {
        return Futures
                .transform(voteHistoryMap.get(user.getPublicKey().getHash()),
                        (AsyncFunction<SignedVoteHistory, List<Vote>>) history -> history == null
                                ? Futures.immediateFuture(Collections.emptyList())
                                : Futures
                                        .transform(
                                                Futures.successfulAsList(
                                                        history.getHistory().getContentHashList()
                                                                .stream()
                                                                .map(hash -> getUserVote(hash,
                                                                        user))
                                                        .collect(Collectors.toList())),
                                        (Function<List<Vote>, List<Vote>>) votes -> votes.stream()
                                                .filter(Objects::nonNull)
                                                .collect(Collectors.toList())));
    }

    @Override
    public ListenableFuture<Integer> getVoteHistorySize(User user) {
        return Futures.transform(voteHistoryMap.get(user.getPublicKey().getHash()),
                (Function<SignedVoteHistory, Integer>) history -> history == null ? 0
                        : history.getHistory().getContentHashCount());
    }

    @Override
    public Future<Boolean> castVote(Vote vote) throws SignatureException, InvalidKeyException {
        return importService.updateVote(vote);
    }
}
