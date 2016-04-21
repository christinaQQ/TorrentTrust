package moe.cdn.cweb.dht.storage;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.dht.storage.annotations.BackingFile;
import moe.cdn.cweb.dht.storage.annotations.UnstartedStorageMaintenance;
import net.tomp2p.dht.Storage;

/**
 * @author davix
 */
public class StorageModule extends AbstractModule {

    @Provides
    static ValidatedStorageLayer provideValidatedStorageLayer(
            Storage storage,
            IncomingDataValidator incomingDataValidator,
            @UnstartedStorageMaintenance ScheduledExecutorService scheduledExecutorService) {
        ValidatedStorageLayer storageLayer = new ValidatedStorageLayer(storage,
                incomingDataValidator);
        storageLayer.start(scheduledExecutorService, storageLayer.storageCheckIntervalMillis());
        return storageLayer;
    }

    @Override
    protected void configure() {
        // TODO: implement persistent storage
        bind(ScheduledExecutorService.class).annotatedWith(UnstartedStorageMaintenance.class)
                .toInstance(Executors.newSingleThreadScheduledExecutor());
        bind(File.class).annotatedWith(BackingFile.class).toInstance(new File("storage.object"));
        bind(Storage.class).to(FileBackedStorage.class).in(Singleton.class);
    }
}
