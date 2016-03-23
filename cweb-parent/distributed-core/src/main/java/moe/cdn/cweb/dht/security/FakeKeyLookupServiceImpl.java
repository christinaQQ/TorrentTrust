package moe.cdn.cweb.dht.security;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.security.KeyLookupService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

class FakeKeyLookupServiceImpl implements KeyLookupService {

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

    @Override
    public ListenableFuture<Optional<SignedUser>> findOwner(Key publicKey) {
        return Futures.immediateFuture(Optional.ofNullable(keyserver.get(publicKey.getHash())));
    }

    @Override
    public Optional<Key> findKey(Hash keyHash) {
        if (!keyserver.containsKey(keyHash)) {
            return Optional.empty();
        } else {
            return Optional.of(keyserver.get(keyHash).getUser().getPublicKey());
        }
    }

    @Override
    public Future<Void> shutdown() {
        return Futures.immediateFuture(null);
    }

}
