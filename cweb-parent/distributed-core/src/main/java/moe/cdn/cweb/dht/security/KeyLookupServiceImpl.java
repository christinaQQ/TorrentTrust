package moe.cdn.cweb.dht.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Provider;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.annotations.KeyLookup;

class KeyLookupServiceImpl implements KeyLookupService {

    private final Provider<CwebMap<SignedUser>> keyServiceCwebMapProvider;

    @Inject
    public KeyLookupServiceImpl(
            @KeyLookup Provider<CwebMap<SignedUser>> keyServiceCwebMapProvider) {
        this.keyServiceCwebMapProvider = checkNotNull(keyServiceCwebMapProvider);
    }

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        return Futures.transform(keyServiceCwebMapProvider.get().all(publicKey.getHash()),
                (Function<Collection<SignedUser>, Optional<SignedUser>>) signedUsers -> {
                    Collection<SignedUser> records = signedUsers.stream()
                            .filter(u -> u.getUser().getPublicKey().equals(publicKey))
                            .collect(Collectors.toList());
                    if (records.isEmpty()) {
                        return Optional.empty();
                    } else if (records.size() > 1) {
                        throw new KeyLookupServiceException(
                                "Inconsistent security state. Multiple users with the "
                                        + "same public key.");
                    } else {
                        return Optional.of(Iterables.getOnlyElement(records));
                    }
                });
    }

    @Override
    public ListenableFuture<Optional<Key>> findKey(Hash keyHash) {
        return Futures.transform(keyServiceCwebMapProvider.get().all(keyHash),
                (Function<Collection<SignedUser>, Optional<Key>>) signedUsers -> {
                    if (signedUsers.isEmpty()) {
                        return Optional.empty();
                    } else if (signedUsers.size() > 1) {
                        throw new KeyLookupServiceException(
                                "Inconsistent security state. Multiple keys with the same hash.");
                    } else {
                        return Optional
                                .of(Iterables.getOnlyElement(signedUsers).getUser().getPublicKey());
                    }
                });
    }

}
