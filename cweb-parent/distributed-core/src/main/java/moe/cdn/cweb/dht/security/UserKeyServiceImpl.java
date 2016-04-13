package moe.cdn.cweb.dht.security;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.dht.annotations.KeyLookup;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.security.utils.Representations;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Provider;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static moe.cdn.cweb.SecurityProtos.*;

class UserKeyServiceImpl implements UserKeyService {
    private static final Logger logger = LogManager.getLogger();

    private final Provider<CwebMultiMap<SignedUser>> keyServiceCwebMapProvider;
    private final KeyEnvironment keyEnvironment;

    @Inject
    public UserKeyServiceImpl(
            @KeyLookup Provider<CwebMultiMap<SignedUser>> keyServiceCwebMapProvider,
            KeyEnvironment keyEnvironment) {
        this.keyServiceCwebMapProvider = checkNotNull(keyServiceCwebMapProvider);
        this.keyEnvironment = keyEnvironment;
    }

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        logger.debug("Looking up owner of key {}", Representations.asString(publicKey));
        return Futures.transform(keyServiceCwebMapProvider.get().all(publicKey.getHash()),
                (Function<Collection<SignedUser>, Optional<SignedUser>>) signedUsers -> {
                    Collection<SignedUser> records = signedUsers.stream()
                            .filter(u -> u.getUser().getPublicKey().equals(publicKey))
                            .collect(Collectors.toList());
                    if (records.isEmpty()) {
                        logger.debug("Key owner not found.");
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
        logger.debug("Looking up key with hash {}", Representations.asString(keyHash));
        return Futures.transform(keyServiceCwebMapProvider.get().all(keyHash),
                (Function<Collection<SignedUser>, Optional<Key>>) signedUsers -> {
                    if (signedUsers.isEmpty()) {
                        logger.debug("Key not found.");
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

    @Override
    public ListenableFuture<Boolean> addTrustAssertion(User.TrustAssertion trustAssertion) {
        logger.debug("Adding assertion to trust network: {}", trustAssertion);
        Hash hash = keyEnvironment.getKeyPair().getPublicKey().getHash();
        CwebMultiMap<SignedUser> signedUserCwebMultiMap = keyServiceCwebMapProvider.get();
        return Futures.transform(signedUserCwebMultiMap.get(hash),
                (AsyncFunction<SignedUser, Boolean>) self -> {
                    if (self == null) {
                        User localUser = keyEnvironment.getLocalUser();
                        self = SignedUser.newBuilder()
                                .setSignature(SignatureUtils.signMessage(keyEnvironment
                                                .getKeyPair(),
                                        localUser))
                                .setUser(localUser).build();
                    }
                    SignedUser.newBuilder(self).setUser(User.newBuilder(self.getUser())
                            .addTrusted(trustAssertion));
                    return signedUserCwebMultiMap.put(hash, self);
                });
    }
}
