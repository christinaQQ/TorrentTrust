package moe.cdn.cweb.dht.internal.tomp2pcompat;

import com.google.common.util.concurrent.ListenableFuture;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Wraps a {@link BaseFuture TomP2P future} as a {@link ListenableFuture}.
 *
 * @param <V> the value to be returned in a {@link ListenableFuture}
 * @param <F> the TomP2P future
 * @author davix
 */
public abstract class BaseFutureAsListenableFuture<V, F extends BaseFuture>
        implements ListenableFuture<V> {
    protected final F baseFuture;
    private volatile boolean cancelled;
    private volatile boolean done;

    public BaseFutureAsListenableFuture(F baseFuture) {
        this.baseFuture = baseFuture;
        baseFuture.addListener(new BaseFutureListener<BaseFuture>() {
            @Override
            public void operationComplete(BaseFuture future) throws Exception {
                done = true;
            }

            @Override
            public void exceptionCaught(Throwable t) throws Exception {
                done = true;
            }
        });
    }

    @Override
    public void addListener(Runnable runnable, Executor executor) {
        baseFuture.addListener(new ListenerWrapper(executor, runnable));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (baseFuture) {
            if (cancelled) {
                return false;
            }
            baseFuture.cancel();
            cancelled = true;
            return true;
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return cancelled || done;
    }

    /**
     * Converts this object to the value.
     *
     * @return the value
     */
    abstract protected V toValueAfterGet();

    @Override
    public V get() throws InterruptedException, ExecutionException {
        baseFuture.await();
        return toValueAfterGet();
    }

    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        boolean ok = baseFuture.await(unit.toMillis(timeout));
        if (!ok) {
            throw new TimeoutException();
        }
        return toValueAfterGet();
    }
}
