package moe.cdn.cweb;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import moe.cdn.cweb.dht.security.KeyLookupService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

/**
 * @author eyeung
 */
public class TrustApiImpl implements TrustApi {
    private static final Logger logger = LogManager.getLogger();
    private final CwebApi cwebApi;
    private final KeyLookupService keyLookupService;
    private final TrustGenerator trustGenerator;

    @Inject
    public TrustApiImpl(CwebApi cwebApi, KeyLookupService keyLookupService,
                        TrustGenerator trustGenerator) {
        this.cwebApi = cwebApi;
        this.keyLookupService = keyLookupService;
        this.trustGenerator = trustGenerator;
    }

    @Override
    public double trustForObject(TorrentTrustProtos.User user,
                                 TorrentTrustProtos.Vote.Assertion assertion,
                                 SecurityProtos.Hash objectHash, TrustMetric trustMetric)
                                throws CwebApiException, ExecutionException, InterruptedException {
        List<TorrentTrustProtos.Vote> votesOnObject;
        // get the votes on the object
        try {votesOnObject = cwebApi.getVotes(objectHash);
        } catch (CwebApiException e) {
            return 0.0;
        }
        if (votesOnObject == null) {
            return 0.0;
        }
        // get the users who voted on the object
        double  score = 0.0;
        for (TorrentTrustProtos.Vote v : votesOnObject) {
            SecurityProtos.Key ownerKey = v.getOwnerPublicKey();
            Optional<TorrentTrustProtos.SignedUser> someVotingUser = keyLookupService.findOwner(ownerKey).get();

            if (!someVotingUser.isPresent()) {
                throw new CwebApiException("Found invalid user vote on object.");
            }
            TorrentTrustProtos.SignedUser votingUser = someVotingUser.get();
            if (votingUser.getUser() == user) {
                continue;
            }

            // now calculate the score, sum over these users of the
            // correlation coefficient, trust score, and vote agreement

            double userCorrelation = trustGenerator.correlationCoefficient(user, votingUser.getUser());
            double trustScore = trustGenerator.trustCoefficient(user, votingUser.getUser(), trustMetric);
            if (v.getAssertionList().isEmpty()) {
                throw new CwebApiException("No available assertions for a vote cast on object!");
            }
            double agreement = v.getAssertion(0).getRatingValue() == assertion.getRatingValue() ? 1.0 : -1.0;
            score = score + userCorrelation * trustScore * agreement;
        }


        return score;
    }
}
