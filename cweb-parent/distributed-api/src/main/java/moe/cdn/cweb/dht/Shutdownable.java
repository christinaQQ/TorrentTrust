package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Indicates that something can be shutdown
 *
 * @author jim
 */
public interface Shutdownable {
    /**
     * Performs a shutdown operation
     *
     * @return a future that is resolved when the shutdown completes
     */
    ListenableFuture<Void> shutdown();
}
