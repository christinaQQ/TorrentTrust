package moe.cdn.cweb.dht;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface CwebFutureGet<T> {
    /**
     * Blocks until a collection of content can be returned
     * 
     * @return
     * @throws InterruptedException
     */
    Collection<T> getAll() throws InterruptedException;

    /**
     * Blocks for {@code timeout} until a collection of content can be returned
     * 
     * @return
     * @throws InterruptedException
     * @throws TimeoutException
     */
    Collection<T> getAll(long timeout, TimeUnit units)
            throws InterruptedException, TimeoutException;
}
