package moe.cdn.cweb;

import com.google.inject.Inject;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.trust.CwebTrustNetworkService;
import moe.cdn.cweb.vote.CwebVoteService;

import java.util.List;
import java.util.stream.Collectors;

class CwebApiImpl implements CwebApi {

    private final CwebTrustNetworkService trustNetworkService;
    private final CwebVoteService voteService;

    @Inject
    public CwebApiImpl(CwebTrustNetworkService trustNetworkService, CwebVoteService voteService) {
        this.trustNetworkService = trustNetworkService;
        this.voteService = voteService;
    }

    @Override
    public List<Vote> getVotesForUser(User user) {
        return null;
    }

    @Override
    public List<User> getTrustedUsersForUser(User user) {
        return trustNetworkService.getLocalTrustNetwork(user).stream().collect(Collectors.toList());
    }

    @Override
    public List<Vote> getVotes(Hash objectHash) {
        return voteService.getAllVotes(objectHash).stream().collect(Collectors.toList());
    }

}
