package moe.cdn.cweb.dht.security;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class FakeKeyLookupServiceImpl implements KeyLookupService {

    private static final Logger logger = LogManager.getLogger();
    private final Map<Hash, SignedUser> keyserver;

    public FakeKeyLookupServiceImpl() {
        this.keyserver = new HashMap<>();
    }

    public FakeKeyLookupServiceImpl(List<SignedUser> records) {
        this();
        for (TorrentTrustProtos.SignedUser record : records) {
            keyserver.put(record.getUser().getPublicKey().getHash(), record);
        }
    }

    private static void warnDeprecation() {
        logger.warn("Deprecated: FakeKeyLookupService is deprecated.");
    }

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        warnDeprecation();
        return Futures.immediateFuture(Optional.ofNullable(keyserver.get(publicKey.getHash())));
    }

    @Override
    public ListenableFuture<Optional<Key>> findKey(Hash keyHash) {
        warnDeprecation();
        if (!keyserver.containsKey(keyHash)) {
            return Futures.immediateFuture(Optional.empty());
        } else {
            return Futures
                    .immediateFuture(Optional.of(keyserver.get(keyHash).getUser().getPublicKey()));
        }
    }
}
