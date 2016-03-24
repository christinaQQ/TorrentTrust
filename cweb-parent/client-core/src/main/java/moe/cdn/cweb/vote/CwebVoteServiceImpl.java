package moe.cdn.cweb.vote;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
    public List<Vote> getAllVotes(Hash objectHash) {
        try {
            return voteMap.all(objectHash).get().stream()
                    .filter(signatureValidationService::validateVote).map(SignedVote::getVote)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean castVote(Vote vote) {
        return importService.importVote(vote);
    }

}
