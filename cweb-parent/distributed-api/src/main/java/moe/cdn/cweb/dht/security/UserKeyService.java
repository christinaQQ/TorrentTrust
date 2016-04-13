package moe.cdn.cweb.dht.security;

import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;

import java.util.Optional;

import static moe.cdn.cweb.TorrentTrustProtos.User;

public interface UserKeyService {
    /**
     * Finds the owner of a certain key
     *
     * @param publicKey
     * @return future producing either a user record or nothing
     * @throws KeyLookupServiceException if there are multiple users found for
     *                                   one key or if the operation was interrupted.
     */
    ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey);

    /**
     * Finds all keys that hash to the provided hash
     *
     * @param keyHash hash of the public key
     * @return future producing either a public key or nothing
     * @throws KeyLookupServiceException if there are multiple keys for the hash
     *                                   or if the operation was interrupted.
     */
    ListenableFuture<Optional<Key>> findKey(Hash keyHash);

    /**
     * Adds an assertion about a key to the trust network of the current user.
     *
     * @param trustAssertion the trust assertion
     * @return a future of {@code true} if the user is successfully updated
     */
    ListenableFuture<Boolean> addTrustAssertion(User.TrustAssertion trustAssertion);
}
