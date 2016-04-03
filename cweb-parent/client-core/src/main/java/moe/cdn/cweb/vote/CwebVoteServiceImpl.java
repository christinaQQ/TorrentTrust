package moe.cdn.cweb.vote;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;
import moe.cdn.cweb.security.CwebImportService;

class CwebVoteServiceImpl implements CwebVoteService {

    private final CwebSignatureValidationService signatureValidationService;
    private final CwebImportService importService;
    private final CwebMultiMap<SignedVote> voteMap;

    @Inject
    public CwebVoteServiceImpl(CwebSignatureValidationService signatureValidationService,
                               CwebImportService importService,
                               @VoteDomain CwebMultiMap<SignedVote> voteMap) {
        this.signatureValidationService = checkNotNull(signatureValidationService);
        this.importService = checkNotNull(importService);
        this.voteMap = checkNotNull(voteMap);
    }

    @Override
    public ListenableFuture<Collection<Vote>> getAllVotes(Hash objectHash) {
        return Futures.transform(voteMap.all(objectHash),
                (Function<Collection<SignedVote>, List<Vote>>) signedVotes -> signedVotes.stream()
                        .filter(signatureValidationService::validateVote).map(SignedVote::getVote)
                        .collect(Collectors.toList()));
    }

    @Override
    public Future<Boolean> castVote(Vote vote) throws SignatureException, InvalidKeyException {
        return importService.addVote(vote);
    }
}
