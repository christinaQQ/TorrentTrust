package moe.cdn.cweb;

import java.util.concurrent.ExecutionException;

/**
 * @author eyeung
 */
public interface TrustApi {

    double trustForObject(TorrentTrustProtos.User user,
                          TorrentTrustProtos.Vote.Assertion assertion,
                          SecurityProtos.Hash objectHash, TrustMetric trustMetric)
            throws CwebApiException, ExecutionException, InterruptedException;


    enum TrustMetric {
        ONLY_FRIENDS,
        FRIENDS_OF_FRIENDS,
        CONNECTED_COMPONENT,
        EIGENTRUST
    }
}
