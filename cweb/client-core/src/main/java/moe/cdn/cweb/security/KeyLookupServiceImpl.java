package moe.cdn.cweb.security;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.dht.CwebMap;

public class KeyLookupServiceImpl implements KeyLookupService {

    private final CwebMap<Hash, SignedUserRecord> keyServiceDht;

    @Inject
    public KeyLookupServiceImpl(CwebMap<Hash, SignedUserRecord> keyServiceDht) {
        this.keyServiceDht = keyServiceDht;
    }

    @Override
    public Optional<SignedUserRecord> findOwner(Key publicKey) {
        try {
            Collection<SignedUserRecord> records = keyServiceDht.get(publicKey.getHash()).getAll()
                    .stream().filter(u -> u.getUser().getPublicKey().equals(publicKey))
                    .collect(Collectors.toList());
            if (records.isEmpty()) {
                return Optional.empty();
            } else if (records.size() > 1) {
                throw new KeyLookupServiceException(
                        "Inconsistent security state. Multiple users with the same public key.");
            } else {
                return Optional.of(Iterables.getOnlyElement(records));
            }
        } catch (InterruptedException e) {
            throw new KeyLookupServiceException("Lookup was interrupted.");
        }
    }

    @Override
    public Optional<Key> findKey(Hash keyHash) {
        try {
            Collection<SignedUserRecord> records = keyServiceDht.get(keyHash).getAll();
            if (records.isEmpty()) {
                return Optional.empty();
            } else if (records.size() > 1) {
                throw new KeyLookupServiceException("Multiple users under this hash.");
            } else {
                return Optional.of(Iterables.getOnlyElement(records).getUser().getPublicKey());
            }
        } catch (InterruptedException e) {
            throw new KeyLookupServiceException("Lookup was interrupted.");
        }
    }

}
