package moe.cdn.cweb.dht.internal.tomp2pcompat;

import com.google.common.util.concurrent.ListenableFuture;
import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.dht.DHTBuilder;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.futures.*;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author davix
 */
public class FutureGetWrapper implements ListenableFuture<GetResponse> {
    private final FutureGet underlying;
    private volatile boolean cancelled;

    public FutureGetWrapper(FutureGet underlying) {
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
     * @param futureResponse The futurRepsonse that has been created
     */
    public FutureGet addRequests(FutureResponse futureResponse) {
        return underlying.addRequests(futureResponse);
    }

    /**
     * Adds a listener to the response future and releases all aquired channels in channel creator.
     *
     * @param channelCreator The channel creator that will be shutdown and all connections will
     *                       be closed
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
     * @param futureRouting The future object to set
     */
    public void futureRouting(FutureRouting futureRouting) {
        underlying.futureRouting(futureRouting);
    }

    public FutureDone<Void> futuresCompleted() {
        return underlying.futuresCompleted();
    }

    public FutureGet await() throws InterruptedException {
        return underlying.await();
    }

    public FutureGet awaitUninterruptibly() {
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

    public boolean isSuccess() {
        return underlying.isSuccess();
    }

    public boolean isFailed() {
        return underlying.isFailed();
    }

    public FutureGet failed(BaseFuture origin) {
        return underlying.failed(origin);
    }

    public FutureGet failed(String failed, BaseFuture origin) {
        return underlying.failed(failed, origin);
    }

    public FutureGet failed(Throwable t) {
        return underlying.failed(t);
    }

    public FutureGet failed(String failed, Throwable t) {
        return underlying.failed(failed, t);
    }

    public FutureGet failed(String failed) {
        return underlying.failed(failed);
    }

    public String failedReason() {
        return underlying.failedReason();
    }

    public BaseFuture.FutureType type() {
        return underlying.type();
    }

    public FutureGet awaitListeners() throws InterruptedException {
        return underlying.awaitListeners();
    }

    public FutureGet awaitListenersUninterruptibly() {
        return underlying.awaitListenersUninterruptibly();
    }

    public FutureGet addListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.addListener(listener);
    }

    public FutureGet removeListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.removeListener(listener);
    }

    public FutureGet addCancel(Cancel cancelListener) {
        return underlying.addCancel(cancelListener);
    }

    public FutureGet removeCancel(Cancel cancelListener) {
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
    public GetResponse get() throws InterruptedException, ExecutionException {
        return new Result(underlying.await());
    }

    @Override
    public GetResponse get(long timeout, TimeUnit unit) throws InterruptedException,
            TimeoutException {
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

    /**
     * @author davix
     */
    public static class Result implements GetResponse {
        private final FutureGet futureGet;

        public Result(FutureGet futureGet) {
            this.futureGet = futureGet;
        }

        @Override
        public void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData, Map<PeerAddress,
                DigestResult> rawDigest, Map<PeerAddress, Byte> rawStatus, FutureDone<Void>
                                         futuresCompleted) {
            futureGet.receivedData(rawData, rawDigest, rawStatus, futuresCompleted);
        }

        @Override
        public Map<PeerAddress, Map<Number640, Data>> rawData() {
            return futureGet.rawData();
        }

        @Override
        public Map<PeerAddress, DigestResult> rawDigest() {
            return futureGet.rawDigest();
        }

        @Override
        public Map<PeerAddress, Byte> rawStatus() {
            return futureGet.rawStatus();
        }

        @Override
        public DigestResult digest() {
            return futureGet.digest();
        }

        @Override
        public Map<Number640, Data> dataMap() {
            return futureGet.dataMap();
        }

        @Override
        public Data data() {
            return futureGet.data();
        }

        @Override
        public boolean isMinReached() {
            return futureGet.isMinReached();
        }

        @Override
        public boolean isEmpty() {
            return futureGet.isEmpty();
        }
    }
}
