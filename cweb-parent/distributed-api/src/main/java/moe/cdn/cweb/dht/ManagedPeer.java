package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * A peer that that can be managed.
 *
 * @author jim
 */
public interface ManagedPeer extends Shutdownable {
    /**
     * Sets the replication factor. Setting {@code replicationFactor} to 0
     * disables replication.
     *
     * @param replicationFactor non-negative integer value
     * @throws IllegalArgumentException if the replicationFactor is negative
     */
    ListenableFuture<Void> setReplication(int replicationFactor);

    /**
     * Performs bootstrapping with some address
     *
     * @param address to bootstrap to
     */
    ListenableFuture<Void> bootstrapTo(DhtPeerAddress address);

    /**
     * Performs bootstrapping with some addresses
     *
     * @param addresses to bootstrap to
     * @throws IllegalArgumentException if input is null
     */
    ListenableFuture<Void> bootstrapTo(Collection<DhtPeerAddress> addresses);

    /**
     * Performs bootstrapping with some address and awaits until bootstrapping
     * is completed
     *
     * @param address to bootstrap to
     */
    void bootstrapToSync(DhtPeerAddress address);

    /**
     * Performs bootstrapping with some addresses and awaits until bootstrapping
     * is completed
     *
     * @param addresses to bootstrap to
     * @throws IllegalArgumentException if input is null
     */
    void bootstrapToSync(Collection<DhtPeerAddress> addresses);

    /**
     * Gets the address and id associated with this peer
     *
     * @return
     */
    DhtPeerAddress getAddress();
}
