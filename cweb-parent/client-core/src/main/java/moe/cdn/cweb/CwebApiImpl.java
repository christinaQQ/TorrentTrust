package moe.cdn.cweb;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

class CwebApiImpl implements CwebApi {

    private final CwebTrustNetworkApi trustNetworkService;
    private final CwebVoteApi voteService;

    @Inject
    public CwebApiImpl(CwebTrustNetworkApi trustNetworkService, CwebVoteApi voteService) {
        this.trustNetworkService = trustNetworkService;
        this.voteService = voteService;
    }

    @Override
    public List<Vote> getVotesForUser(User user) throws CwebApiException {
        try {
            return voteService.getVoteHistory(user).get().stream().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new CwebApiException(e);
        }
    }

    @Override
    public List<User> getTrustedUsersForUser(User user) throws CwebApiException {
        try {
            return trustNetworkService.getLocalTrustNetwork(user).get().stream()
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new CwebApiException(e);
        }
    }

    @Override
    public List<Vote> getVotes(Hash objectHash) throws CwebApiException {
        try {
            return voteService.getAllVotes(objectHash).get().stream().collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new CwebApiException(e);
        }
    }

}
