package moe.cdn.cweb.dht.security;

import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;

import java.util.Optional;

public interface KeyLookupService {
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
}
