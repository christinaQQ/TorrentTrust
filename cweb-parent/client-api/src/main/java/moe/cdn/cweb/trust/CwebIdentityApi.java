package moe.cdn.cweb.trust;

import java.security.PublicKey;
import java.util.Optional;

import com.google.common.util.concurrent.ListenableFuture;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;

public interface CwebIdentityApi {
    /**
     * Fetches the identity of the current user.
     *
     * @return optional containing user object if current user is registered in
     *         the network.
     */
    ListenableFuture<Optional<User>> getUserIdentity();

    /**
     * Fetches the identity of a certain user who has publicKey as their
     * {@link PublicKey}
     *
     * @return optional containing user object if current user is registered in
     *         the network.
     */
    ListenableFuture<Optional<User>> getUserIdentity(Key publicKey);

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
     * Registers an existing identity from a provided keypair
     * 
     * @param handle handle to assign
     * @param keyPair key pair used for the user and for signing
     * @return boolean indicator checking whether the registration failed or not
     */
    ListenableFuture<Boolean> registerExistingUserIdentity(String handle, KeyPair keyPair);
}
