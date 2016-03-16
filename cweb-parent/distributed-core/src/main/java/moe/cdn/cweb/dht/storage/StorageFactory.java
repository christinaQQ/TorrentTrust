package moe.cdn.cweb.dht.storage;

import net.tomp2p.dht.Storage;

/**
 * @author davix
 */
public interface StorageFactory {
    Storage create();
}
