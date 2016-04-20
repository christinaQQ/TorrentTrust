package moe.cdn.cweb;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author eyeung
 */
public interface TrustApi {

     double TrustForObject(TorrentTrustProtos.User user,
                                 TorrentTrustProtos.Vote.Assertion assertion,
                                 SecurityProtos.Hash objectHash, TrustMetric trustMetric)
            throws CwebApiException, ExecutionException, InterruptedException;


    enum TrustMetric {
        IMMEDIATE,
        NETWORK,
        CENTRALITY
    }
}
