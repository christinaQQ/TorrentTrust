package moe.cdn.cweb.security;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.annotations.UserDomain;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

class KeyLookupServiceImpl implements KeyLookupService {

    private final CwebMap<Hash, SignedUser> keyServiceDht;

    @Inject
    public KeyLookupServiceImpl(@UserDomain CwebMap<Hash, SignedUser> keyServiceDht) {
        this.keyServiceDht = checkNotNull(keyServiceDht);
    }

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        return Futures.transform(keyServiceDht.all(publicKey.getHash()),
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
            Collection<SignedUser> records = keyServiceDht.all(keyHash).get();
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

}
