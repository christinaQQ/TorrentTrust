package moe.cdn.cweb.dht;

import java.util.concurrent.Future;

public interface Shutdownable {
    /**
     * Performs a shutdown operation
     * 
     * @return a future that is resolved when the shutdown completes
     */
    Future<Void> shutdown();
}
