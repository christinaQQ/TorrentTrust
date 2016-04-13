package moe.cdn.cweb.trust;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;

import moe.cdn.cweb.TorrentTrustProtos.User;

public interface CwebTrustNetworkApi {
    /**
     * Fetches the local trust network of a user. Returns an empty collection if
     * the user does not exist.
     * 
     * @param user user that we want to check the trust network for
     * @return future of collection of users belonging to the user's trust
     *         network
     */
    ListenableFuture<Collection<User>> getLocalTrustNetwork(User user);

    /**
     * Adds a user to the current user's trust network. If the user does not
     * exist, the result will be false.
     * 
     * @param user user being added
     * @return future of boolean indicator of whether the adding of trust
     *         completed successfully
     */
    ListenableFuture<Boolean> addUserAsTrusted(User user);
}
