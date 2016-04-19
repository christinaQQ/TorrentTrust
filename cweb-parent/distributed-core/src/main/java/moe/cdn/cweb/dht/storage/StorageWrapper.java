package moe.cdn.cweb.dht.storage;

import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number320;
import net.tomp2p.peers.Number480;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.security.PublicKey;
import java.util.Collection;
import java.util.NavigableMap;

/**
 * Delegates all calls to the underlying {@link Storage} object.
 *
 * @author davix
 */
class StorageWrapper implements Storage {
    private final Storage storage;

    public StorageWrapper(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Data put(Number640 key, Data value) {
        return storage.put(key, value);
    }

    @Override
    public Data get(Number640 key) {
        return storage.get(key);
    }

    @Override
    public boolean contains(Number640 key) {
        return storage.contains(key);
    }

    @Override
    public int contains(Number640 from, Number640 to) {
        return storage.contains(from, to);
    }

    @Override
    public Data remove(Number640 key, boolean returnData) {
        return storage.remove(key, returnData);
    }

    @Override
    public NavigableMap<Number640, Data> remove(Number640 from, Number640 to) {
        return storage.remove(from, to);
    }

    @Override
    public NavigableMap<Number640, Data> subMap(Number640 from,
                                                Number640 to,
                                                int limit,
                                                boolean ascending) {
        return storage.subMap(from, to, limit, ascending);
    }

    @Override
    public NavigableMap<Number640, Data> map() {
        return storage.map();
    }

    @Override
    public void close() {
        storage.close();
    }

    @Override
    public void addTimeout(Number640 key, long expiration) {
        storage.addTimeout(key, expiration);
    }

    @Override
    public void removeTimeout(Number640 key) {
        storage.removeTimeout(key);
    }

    @Override
    public Collection<Number640> subMapTimeout(long to) {
        return storage.subMapTimeout(to);
    }

    @Override
    public int storageCheckIntervalMillis() {
        return storage.storageCheckIntervalMillis();
    }

    @Override
    public boolean protectDomain(Number320 key, PublicKey publicKey) {
        return storage.protectDomain(key, publicKey);
    }

    @Override
    public boolean isDomainProtectedByOthers(Number320 key, PublicKey publicKey) {
        return storage.isDomainProtectedByOthers(key, publicKey);
    }

    @Override
    public boolean protectEntry(Number480 key, PublicKey publicKey) {
        return storage.protectEntry(key, publicKey);
    }

    @Override
    public boolean isEntryProtectedByOthers(Number480 key, PublicKey publicKey) {
        return storage.isEntryProtectedByOthers(key, publicKey);
    }

    @Override
    public Number160 findPeerIDsForResponsibleContent(Number160 locationKey) {
        return storage.findPeerIDsForResponsibleContent(locationKey);
    }

    @Override
    public Collection<Number160> findContentForResponsiblePeerID(Number160 peerID) {
        return storage.findContentForResponsiblePeerID(peerID);
    }

    @Override
    public boolean updateResponsibilities(Number160 locationKey, Number160 peerId) {
        return storage.updateResponsibilities(locationKey, peerId);
    }

    @Override
    public void removeResponsibility(Number160 locationKey) {
        storage.removeResponsibility(locationKey);
    }
}
