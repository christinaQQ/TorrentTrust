package moe.cdn.cweb.trust;

import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;

import java.util.Collection;
import java.util.Optional;

public interface CwebTrustNetworkApi {
    /**
     * Fetches the identity of the current user.
     *
     * @return optional containing user object if current user is registered in
     *         the network.
     */
    ListenableFuture<Optional<User>> getUserIdentity();

    /**
     * Registers a new identity (keypair) with the handle being the user handle.
     * Returns the a {@link KeyPair} if it was successful otherwise returns an
     * empty optional.
     * <p>
     * If any errors were encountered, the future will fail with such errors.
     *
     * @param handle handle for the user we're adding
     * @return keyPair key pair of user we just added
     */
    ListenableFuture<Optional<KeyPair>> registerNewUserIdentity(String handle);

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
     * @param publicKey user being added
     * @return future of boolean indicating whether adding the user to the trust
     *         network completed successfully
     */
    ListenableFuture<Boolean> addUserAsTrusted(Key publicKey);

    /**
     * Removes a user from the current user's trust network.
     *
     * @param publicKey user being removed
     * @return future of boolean indicating whether removing the user from the
     *         trust network completed successfully.
     */
    ListenableFuture<Boolean> removeUserAsTrusted(Key publicKey);
}
