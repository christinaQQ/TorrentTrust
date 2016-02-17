package moe.cdn.cweb.vote;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
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
            return voteMap.all(objectHash).get().stream()
                    .filter(signatureValidationService::validateVote)
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
}
