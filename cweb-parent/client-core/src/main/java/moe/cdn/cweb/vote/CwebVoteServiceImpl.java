package moe.cdn.cweb.vote;

import com.google.inject.Inject;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

class CwebVoteServiceImpl implements CwebVoteService {

    private final CwebSignatureValidationService signatureValidationService;
    private final CwebMap<SignedVote> voteMap;

    @Inject
    public CwebVoteServiceImpl(CwebSignatureValidationService signatureValidationService,
                               @VoteDomain CwebMap<SignedVote> voteMap) {
        this.signatureValidationService = checkNotNull(signatureValidationService);
        this.voteMap = checkNotNull(voteMap);
    }

    @Override
    public List<Vote> getAllVotes(Hash objectHash) {
        try {
            return voteMap.all(objectHash).get().stream()
                    .filter(signatureValidationService::validateVote)
                    .map(SignedVote::getVote)
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean castVote(SignedVote vote) {
        if (!signatureValidationService.validateVote(vote)) {
            return false;
        }
        try {
            return voteMap.put(vote.getVote().getContentHash(), vote).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Future<Void> shutdown() {
        return voteMap.shutdown();
    }
}
