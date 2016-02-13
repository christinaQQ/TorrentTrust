package moe.cdn.cweb.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;

public class FakeKeyLookupServiceImpl implements KeyLookupService {

    private final Map<Hash, SignedUserRecord> keyserver;
    
    public FakeKeyLookupServiceImpl() {
        this.keyserver = new HashMap<>();
    }

    public FakeKeyLookupServiceImpl(List<SignedUserRecord> records) {
        this();
        for (SignedUserRecord record : records) {
            keyserver.put(record.getUser().getPublicKey().getHash(), record);
        }
    }

    @Override
    public Optional<SignedUserRecord> findOwner(Key publicKey) {
        return Optional.ofNullable(keyserver.get(publicKey.getHash()));
    }

    @Override
    public Optional<Key> findKey(Hash keyHash) {
        if (!keyserver.containsKey(keyHash)) {
            return Optional.empty();
        } else {
            return Optional.of(keyserver.get(keyHash).getUser().getPublicKey());
        }
    }

}
