package moe.cdn.cweb.dht.storage;

import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.util.NavigableMap;

/**
 * Validates requests that modify the underlying storage.
 *
 * @author davix
 */
class IncomingValidatedStorageWrapper extends StorageWrapper {
    private final IncomingDataValidator incomingDataValidator;

    public IncomingValidatedStorageWrapper(Storage storage,
                                           IncomingDataValidator incomingDataValidator) {
        super(storage);
        this.incomingDataValidator = incomingDataValidator;
    }

    @Override
    public Data remove(Number640 key, boolean returnData) {
        // FIXME: We deny remove requests and allow only update requests. Should we allow removal?
        return null;
    }

    @Override
    public NavigableMap<Number640, Data> remove(Number640 from, Number640 to) {
        // FIXME: We deny remove requests and allow only update requests. Should we allow removal?
        return null;
    }

    @Override
    public void removeTimeout(Number640 key) {
        // FIXME: We deny remove requests and allow only update requests. Should we allow removal?
        // no-op
    }

    @Override
    public Data put(Number640 key, Data value) {
        if (incomingDataValidator.validate(key.domainKey(), value)) {
            return super.put(key, value);
        }
        // TODO: Give a response to indicate validation failed
        return null;
    }
}
