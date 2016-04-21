package moe.cdn.cweb.dht.internal.tomp2pcompat;

import java.util.concurrent.Executor;

import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;

/**
 * @author davix
 */
class ListenerWrapper implements BaseFutureListener<BaseFuture> {
    private final Executor executor;
    private final Runnable listener;

    public ListenerWrapper(Executor executor, Runnable listener) {
        this.executor = executor;
        this.listener = listener;
    }

    @Override
    public void operationComplete(BaseFuture future) throws Exception {
        executor.execute(listener);
    }

    @Override
    public void exceptionCaught(Throwable t) throws Exception {
        executor.execute(listener);
    }
}
