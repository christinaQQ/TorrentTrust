package moe.cdn.cweb.vote;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.annotations.VoteMap;
import moe.cdn.cweb.security.CwebSignatureValidationService;

class CwebVoteServiceImpl implements CwebVoteService {

    private final CwebSignatureValidationService signatureValidationService;
    private final CwebMap<Hash, SignedVote> voteMap;

    @Inject
    public CwebVoteServiceImpl(CwebSignatureValidationService signatureValidationService,
            @VoteMap CwebMap<Hash, SignedVote> voteMap) {
        this.signatureValidationService = signatureValidationService;
        this.voteMap = voteMap;
    }

    @Override
    public Collection<SignedVote> getAllVotes(Hash objectHash) {
        try {
            return voteMap.get(objectHash).getAll().stream()
                    .filter(v -> signatureValidationService.validateVote(v))
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while attempting to get votes.");
        }
    }

    @Override
    public boolean castVote(SignedVote vote) {
        if (!signatureValidationService.validateVote(vote)) {
            return false;
        }
        try {
            return voteMap.put(vote.getVote().getContentHash(), vote).put();
        } catch (InterruptedException e) {
            return false;
        }
    }
}
