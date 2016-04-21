package moe.cdn.cweb.dht.storage;

import com.google.common.base.Preconditions;
import moe.cdn.cweb.dht.storage.annotations.BackingFile;
import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.*;
import java.util.NavigableMap;
import java.util.concurrent.*;

/**
 * @author davix
 */
public class FileBackedStorage extends AbstractStorage implements Storage {

    private static final Logger logger = LogManager.getLogger();
    private final File backingFile;
    private final ConcurrentSkipListMap<Number640, Data> dataMap;
    private final ScheduledExecutorService scheduledExecutorService;

    @Inject
    public FileBackedStorage(@BackingFile File backingFile) {
        this.backingFile = Preconditions.checkNotNull(backingFile);

        ConcurrentSkipListMap<Number640, Data> existingDataMap = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(backingFile))) {
            @SuppressWarnings("unchecked")
            ConcurrentSkipListMap<Number640, Data> o = (ConcurrentSkipListMap<Number640, Data>)
                    ois.readObject();
            existingDataMap = o;
        } catch (IOException | ClassCastException | ClassNotFoundException ignored) {
        }
        if (existingDataMap == null) {
            dataMap = new ConcurrentSkipListMap<>();
        } else {
            dataMap = existingDataMap;
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread th = new Thread();
            th.setName("FileBackedStorage - writer");
            th.setDaemon(true);
            th.setUncaughtExceptionHandler((t, e) ->
                    logger.error("Uncaught exception in " + t, e));
            return th;
        });
        scheduledExecutorService.scheduleWithFixedDelay(new Persister(), 3, 3, TimeUnit.MINUTES);
    }

    @Override
    protected Data removeEntryForKey(Number640 key) {
        return dataMap.remove(key);
    }

    @Override
    public Data put(Number640 key, Data value) {
        return dataMap.put(key, value);
    }

    @Override
    public Data get(Number640 key) {
        return dataMap.get(key);
    }

    @Override
    public boolean contains(Number640 key) {
        return dataMap.containsKey(key);
    }

    @Override
    public int contains(Number640 from, Number640 to) {
        return dataMap.subMap(from, to).size();
    }

    @Override
    public NavigableMap<Number640, Data> remove(Number640 from, Number640 to) {
        // XXX does not have any atomicity guarantees
        ConcurrentNavigableMap<Number640, Data> r = dataMap.clone().subMap(from, to);
        r.keySet().forEach(dataMap::remove);
        return r;
    }

    @Override
    public NavigableMap<Number640, Data> subMap(Number640 from, Number640 to, int limit,
                                                boolean ascending) {
        if (ascending) {
            return dataMap.subMap(from, to);
        }
        return dataMap.descendingMap().subMap(to, from);
    }

    @Override
    public NavigableMap<Number640, Data> map() {
        // not sure what this method is supposed to do
        return dataMap;
    }

    @Override
    public void close() {
        try {
            flush();
        } catch (IOException e) {
            logger.catching(e);
        }
    }

    public void flush() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backingFile))) {
            oos.writeObject(dataMap);
        }
    }

    private class Persister implements Runnable {

        @Override
        public void run() {
            try {
                flush();
            } catch (IOException e) {
                logger.catching(e);
            }
        }
    }
}
