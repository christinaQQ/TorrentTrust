package moe.cdn.cweb.dht.storage;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.dht.storage.annotations.UnvalidatedStorage;
import net.tomp2p.dht.Storage;
import net.tomp2p.dht.StorageMemory;

/**
 * @author davix
 */
public class StorageModule extends AbstractModule {

    @Provides
    @Singleton
    static Storage provideStorage(@UnvalidatedStorage Storage storage,
            IncomingDataValidator incomingDataValidator) {
        return new IncomingValidatedStorageWrapper(storage, incomingDataValidator);
    }

    @Override
    protected void configure() {
        // TODO: implement persistent storage
        bind(Storage.class).annotatedWith(UnvalidatedStorage.class).to(StorageMemory.class);
    }
}
