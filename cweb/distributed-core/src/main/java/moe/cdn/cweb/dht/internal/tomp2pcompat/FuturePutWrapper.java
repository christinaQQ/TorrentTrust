package moe.cdn.cweb.dht.internal.tomp2pcompat;

import com.google.common.util.concurrent.ListenableFuture;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.dht.DHTBuilder;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.futures.*;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author davix
 */
public class FuturePutWrapper implements ListenableFuture<PutResponse> {
    private final FuturePut underlying;
    private volatile boolean cancelled;

    public FuturePutWrapper(FuturePut underlying) {
        this.underlying = underlying;
    }

    /**
     * @return A reference to the builder that contains the data we were looking for
     */
    public DHTBuilder<?> builder() {
        return underlying.builder();
    }

    /**
     * Returns back those futures that are still running. If 6 storage futures are started at the
     * same time and 5 of
     * them finish, and we specified that we are fine if 5 finishes, then futureDHT returns
     * success. However, the future
     * that may still be running is the one that stores the content to the closest peer. For
     * testing this is not
     * acceptable, thus after waiting for futureDHT, one needs to wait for the running futures as
     * well.
     *
     * @return A future that finishes if all running futures are finished.
     */
    public FutureForkJoin<FutureResponse> futureRequests() {
        return underlying.futureRequests();
    }

    /**
     * Adds all requests that have been created for the DHT operations. Those were created after
     * the routing process.
     *
     * @param futureResponse
     *            The futurRepsonse that has been created
     */
    public FuturePut addRequests(FutureResponse futureResponse) {
        return underlying.addRequests(futureResponse);
    }

    /**
     * Adds a listener to the response future and releases all aquired channels in channel creator.
     *
     * @param channelCreator
     *            The channel creator that will be shutdown and all connections will be closed
     */
    public void addFutureDHTReleaseListener(ChannelCreator channelCreator) {
        underlying.addFutureDHTReleaseListener(channelCreator);
    }

    /**
     * Returns the future object that was used for the routing. Before the FutureDHT is used,
     * FutureRouting has to be
     * completed successfully.
     *
     * @return The future object during the previous routing, or null if routing failed completely.
     */
    public FutureRouting futureRouting() {
        return underlying.futureRouting();
    }

    /**
     * Sets the future object that was used for the routing. Before the FutureDHT is used,
     * FutureRouting has to be
     * completed successfully.
     *
     * @param futureRouting
     *            The future object to set
     */
    public void futureRouting(FutureRouting futureRouting) {
        underlying.futureRouting(futureRouting);
    }

    public FutureDone<Void> futuresCompleted() {
        return underlying.futuresCompleted();
    }

    public FuturePut await() throws InterruptedException {
        return underlying.await();
    }

    public FuturePut awaitUninterruptibly() {
        return underlying.awaitUninterruptibly();
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return underlying.await(timeoutMillis);
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return underlying.awaitUninterruptibly(timeoutMillis);
    }

    public boolean isCompleted() {
        return underlying.isCompleted();
    }

    public boolean isFailed() {
        return underlying.isFailed();
    }

    public FuturePut failed(BaseFuture origin) {
        return underlying.failed(origin);
    }

    public FuturePut failed(String failed, BaseFuture origin) {
        return underlying.failed(failed, origin);
    }

    public FuturePut failed(Throwable t) {
        return underlying.failed(t);
    }

    public FuturePut failed(String failed, Throwable t) {
        return underlying.failed(failed, t);
    }

    public FuturePut failed(String failed) {
        return underlying.failed(failed);
    }

    public String failedReason() {
        return underlying.failedReason();
    }

    public BaseFuture.FutureType type() {
        return underlying.type();
    }

    public FuturePut awaitListeners() throws InterruptedException {
        return underlying.awaitListeners();
    }

    public FuturePut awaitListenersUninterruptibly() {
        return underlying.awaitListenersUninterruptibly();
    }

    public FuturePut addListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.addListener(listener);
    }

    public FuturePut removeListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.removeListener(listener);
    }

    public FuturePut addCancel(Cancel cancelListener) {
        return underlying.addCancel(cancelListener);
    }

    public FuturePut removeCancel(Cancel cancelListener) {
        return underlying.removeCancel(cancelListener);
    }

    public void cancel() {
        underlying.cancel();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        underlying.cancel();
        // TODO: tomp2p futures suck
        cancelled = true;
        return true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return underlying.isCompleted();
    }

    @Override
    public PutResponse get() throws InterruptedException {
        return new Result(underlying.await());
    }

    @Override
    public PutResponse get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        boolean ok = underlying.await(unit.toMillis(timeout));
        if (!ok) {
            throw new TimeoutException();
        }
        return new Result(underlying);
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
        underlying.addListener(new ListenerWrapper(executor, listener));
    }

    static class Result implements PutResponse {
        private final FuturePut futurePut;

        public Result(FuturePut futurePut) {
            this.futurePut = futurePut;
        }

        @Override
        public void storedKeys(Map<PeerAddress, Map<Number640, Byte>> rawResult, FutureDone<Void>
                futuresCompleted) {
            futurePut.storedKeys(rawResult, futuresCompleted);
        }

        @Override
        public double avgStoredKeys() {
            return futurePut.avgStoredKeys();
        }

        @Override
        public Map<PeerAddress, Map<Number640, Byte>> rawResult() {
            return futurePut.rawResult();
        }

        @Override
        public boolean isMinReached() {
            return futurePut.isMinReached();
        }

        @Override
        public Map<Number640, Integer> result() {
            return futurePut.result();
        }

        @Override
        public boolean isSuccess() {
            return futurePut.isSuccess();
        }

        @Override
        public boolean isSuccessPartially() {
            return futurePut.isSuccessPartially();
        }
    }

}
