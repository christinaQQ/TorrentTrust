package moe.cdn.cweb.dht.storage;

import net.tomp2p.dht.Storage;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number320;
import net.tomp2p.peers.Number480;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.*;

/**
 * Boilerplate implementation of {@link Storage}. Responsibility and protection logic is based on
 * {@link net.tomp2p.dht.StorageMemory}. Removal of expired entries is done by a worker thread
 * (and is my own work).
 *
 * @author davix
 */
public abstract class AbstractStorage implements Storage {
    private static final Logger logger = LogManager.getLogger();
    // Peer location key responsibilities
    protected final ConcurrentMap<Number160, Set<Number160>> peerIdToLocationKey;
    protected final ConcurrentMap<Number160, Number160> locationKeyToPeerId;
    // Protection information
    protected final ConcurrentMap<Number320, PublicKey> domainToPublicKey;
    protected final ConcurrentMap<Number480, PublicKey> entryToPublicKey;
    // Expired key purging
    protected final ExecutorService purgeExpiredKeysExecutorService;
    protected final DelayQueue<ExpiredKey> expiringKeys;

    protected AbstractStorage() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(),
                new ConcurrentHashMap<>(), Executors.newSingleThreadExecutor());
    }

    /**
     * @param peerIdToLocationKey
     * @param locationKeyToPeerId
     * @param domainToPublicKey
     * @param entryToPublicKey
     * @param purgeExpiredEntriesExecutorService an executor to execute the single thread that
     */
    protected AbstractStorage(ConcurrentMap<Number160, Set<Number160>> peerIdToLocationKey,
                              ConcurrentMap<Number160, Number160> locationKeyToPeerId,
                              ConcurrentMap<Number320, PublicKey> domainToPublicKey,
                              ConcurrentMap<Number480, PublicKey> entryToPublicKey,
                              ExecutorService purgeExpiredEntriesExecutorService) {
        this.peerIdToLocationKey = peerIdToLocationKey;
        this.locationKeyToPeerId = locationKeyToPeerId;
        this.domainToPublicKey = domainToPublicKey;
        this.entryToPublicKey = entryToPublicKey;
        this.purgeExpiredKeysExecutorService = purgeExpiredEntriesExecutorService;
        expiringKeys = new DelayQueue<>();
        Thread expiredKeysReaperThread = new Thread(new ExpiredKeyReaper());
        expiredKeysReaperThread.setUncaughtExceptionHandler((t, e) ->
                logger.error("Uncaught exception in " + t, e));
        expiredKeysReaperThread.setName("AbstractStorage - expired entry remover");
        expiredKeysReaperThread.setDaemon(true);
        expiredKeysReaperThread.start();
    }

    @Override
    public Number160 findPeerIDsForResponsibleContent(Number160 locationKey) {
        return locationKeyToPeerId.get(locationKey);
    }

    @Override
    public Collection<Number160> findContentForResponsiblePeerID(Number160 peerID) {
        return peerIdToLocationKey.get(peerID);
    }

    @Override
    public boolean updateResponsibilities(Number160 locationKey, Number160 peerId) {
        logger.trace("Updating peer {} to be responsible for key {}.", peerId, locationKey);
        final Number160 oldPeerId = locationKeyToPeerId.put(locationKey, peerId);
        final boolean hasChanged;
        if (oldPeerId != null) {
            if (oldPeerId.equals(peerId)) {
                hasChanged = false;
            } else {
                removeRevResponsibility(oldPeerId, locationKey);
                hasChanged = true;
            }
        } else {
            hasChanged = true;
        }
        Set<Number160> contentIDs = peerIdToLocationKey.get(peerId);
        if (contentIDs == null) {
            contentIDs = new HashSet<>();
            peerIdToLocationKey.put(peerId, contentIDs);
        }
        contentIDs.add(locationKey);
        return hasChanged;
    }

    @Override
    public void removeResponsibility(Number160 locationKey) {
        logger.trace("Removing responsibility for location key: {}");
        Number160 peerId = locationKeyToPeerId.remove(locationKey);
        if (peerId != null) {
            removeRevResponsibility(peerId, locationKey);
        }
    }

    private void removeRevResponsibility(Number160 peerId, Number160 locationKey) {
        Set<Number160> contentIDs = peerIdToLocationKey.get(peerId);
        if (contentIDs != null) {
            contentIDs.remove(locationKey);
            if (contentIDs.isEmpty()) {
                peerIdToLocationKey.remove(peerId);
            }
        }
    }

    @Override
    public boolean protectDomain(Number320 key, PublicKey publicKey) {
        domainToPublicKey.put(key, publicKey);
        return true;
    }

    @Override
    public boolean protectEntry(Number480 key, PublicKey publicKey) {
        entryToPublicKey.put(key, publicKey);
        return true;
    }

    @Override
    public boolean isDomainProtectedByOthers(Number320 key, PublicKey publicKey) {
        PublicKey other = domainToPublicKey.get(key);
        return other != null && !other.equals(publicKey);
    }

    @Override
    public boolean isEntryProtectedByOthers(Number480 key, PublicKey publicKey) {
        PublicKey other = entryToPublicKey.get(key);
        return other != null && !other.equals(publicKey);
    }

    @Override
    public void addTimeout(Number640 key, long expiration) {
        expiringKeys.add(new ExpiredKey(key, expiration));
    }

    @Override
    public void removeTimeout(Number640 key) {
        expiringKeys.remove(ExpiredKey.dummy(key));
    }

    /*
     * I think this method is supposed to return a collection of keys that need to be removed.
     */
    @Override
    public Collection<Number640> subMapTimeout(long to) {
        // Return an empty set because we handle purging expired keys ourselves
        return Collections.emptySet();
    }

    @Override
    public int storageCheckIntervalMillis() {
        // We handle purging expired keys, so return a very big number for the poll interval
        return Integer.MAX_VALUE;
    }

    @Override
    public Data remove(Number640 key, boolean returnData) {
        return removeEntryForKey(key);
    }

    protected abstract Data removeEntryForKey(Number640 key);

    private static class ExpiredKey implements Delayed {
        private final Number640 key;
        private final long expiryTimeMillis;

        public ExpiredKey(Number640 key, long expiryTimeMillis) {
            this.key = key;
            this.expiryTimeMillis = expiryTimeMillis;
        }

        public static ExpiredKey dummy(Number640 key) {
            return new ExpiredKey(key, 0);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long timeLeft = expiryTimeMillis - System.currentTimeMillis();
            return unit.convert(timeLeft, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            ExpiredKey other = (ExpiredKey) o;
            long now = System.currentTimeMillis();
            long thisTimeLeft = expiryTimeMillis - now;
            long otherTimeLeft = other.expiryTimeMillis - now;
            return Long.compare(thisTimeLeft, otherTimeLeft);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExpiredKey that = (ExpiredKey) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    private class ExpiredKeyReaper implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ExpiredKey expiredKey = expiringKeys.take();
                    removeEntryForKey(expiredKey.key);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
