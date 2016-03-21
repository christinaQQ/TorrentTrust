package moe.cdn.cweb.dht.security;

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

import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

class KeyLookupServiceImpl implements KeyLookupService {

    private final Provider<CwebMap<SignedUser>> keyServiceCwebMapProvider;

    @Inject
    public KeyLookupServiceImpl(@KeyLookup Provider<CwebMap<SignedUser>> keyServiceCwebMapProvider) {
        this.keyServiceCwebMapProvider = keyServiceCwebMapProvider;
    }

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        return Futures.transform(keyServiceCwebMapProvider.get().all(publicKey.getHash()),
                (Function<Collection<SignedUser>, Optional<SignedUser>>)
                        signedUsers -> {
                            Collection<SignedUser> records = signedUsers.stream().filter(u -> u
                                    .getUser()
                                    .getPublicKey()
                                    .equals(publicKey))
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
    public Optional<Key> findKey(Hash keyHash) {
        try {
            Collection<SignedUser> records = keyServiceCwebMapProvider.get().all(keyHash).get();
            if (records.isEmpty()) {
                return Optional.empty();
            } else if (records.size() > 1) {
                throw new KeyLookupServiceException("Multiple users under this hash.");
            } else {
                return Optional.of(Iterables.getOnlyElement(records).getUser().getPublicKey());
            }
        } catch (InterruptedException e) {
            throw new KeyLookupServiceException("Lookup was interrupted.");
        } catch (ExecutionException e) {
            throw new KeyLookupServiceException(e);
        }
    }

    @Override
    public Future<Void> shutdown() {
        return keyServiceCwebMapProvider.get().shutdown();
    }

}
