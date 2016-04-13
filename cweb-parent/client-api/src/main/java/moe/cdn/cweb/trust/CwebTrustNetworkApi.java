package moe.cdn.cweb.trust;

import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.User;

import java.util.Collection;

public interface CwebTrustNetworkApi {
    /**
     * Fetches the local trust network of a user. Returns an empty collection if
     * the user does not exist.
     *
     * @param user user that we want to check the trust network for
     * @return future of collection of users belonging to the user's trust
     * network
     */
    ListenableFuture<Collection<User>> getLocalTrustNetwork(User user);

    /**
     * Adds a user to the current user's trust network. If the user does not
     * exist, the result will be false.
     *
     * @param publicKey user being added
     * @return future of boolean indicating whether adding the user to the trust network
     * completed successfully
     */
    ListenableFuture<Boolean> addUserAsTrusted(Key publicKey);

    /**
     * Removes a user from the current user's trust network.
     *
     * @param publicKey user being removed
     * @return future of boolean indicating whether removing the user from the trust network
     * completed successfully.
     */
    ListenableFuture<Boolean> removeUserAsTrusted(Key publicKey);
}
