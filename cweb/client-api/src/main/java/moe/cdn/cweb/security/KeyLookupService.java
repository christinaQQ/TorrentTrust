package moe.cdn.cweb.security;

import java.util.Optional;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;

public interface KeyLookupService {
    /**
     * Find the owner of a certain key
     * @param publicKey
     * @return a user record or nothing
     */
    Optional<SignedUserRecord> findOwner(Key publicKey);
    
    /**
     * Find all keys that hash to the provided hash
     * @param keyHash hash of the public key
     * @return a public key or nothing
     */
    Optional<Key> findKey(Hash keyHash);
}
