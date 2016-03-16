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
public class FuturePutWrapper extends BaseFutureAsListenableFuture<PutResponse, FuturePut> {

    public FuturePutWrapper(FuturePut futurePut) {
        super(futurePut);
    }

    /**
     * @return A reference to the builder that contains the data we were looking for
     */
    public DHTBuilder<?> builder() {
        return baseFuture.builder();
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
        return baseFuture.futureRequests();
    }

    /**
     * Adds all requests that have been created for the DHT operations. Those were created after
     * the routing process.
     *
     * @param futureResponse The futurRepsonse that has been created
     */
    public FuturePut addRequests(FutureResponse futureResponse) {
        return baseFuture.addRequests(futureResponse);
    }

    /**
     * Adds a listener to the response future and releases all aquired channels in channel creator.
     *
     * @param channelCreator The channel creator that will be shutdown and all connections will
     *                       be closed
     */
    public void addFutureDHTReleaseListener(ChannelCreator channelCreator) {
        baseFuture.addFutureDHTReleaseListener(channelCreator);
    }

    /**
     * Returns the future object that was used for the routing. Before the FutureDHT is used,
     * FutureRouting has to be
     * completed successfully.
     *
     * @return The future object during the previous routing, or null if routing failed completely.
     */
    public FutureRouting futureRouting() {
        return baseFuture.futureRouting();
    }

    /**
     * Sets the future object that was used for the routing. Before the FutureDHT is used,
     * FutureRouting has to be
     * completed successfully.
     *
     * @param futureRouting The future object to set
     */
    public void futureRouting(FutureRouting futureRouting) {
        baseFuture.futureRouting(futureRouting);
    }

    public FutureDone<Void> futuresCompleted() {
        return baseFuture.futuresCompleted();
    }

    public FuturePut await() throws InterruptedException {
        return baseFuture.await();
    }

    public FuturePut awaitUninterruptibly() {
        return baseFuture.awaitUninterruptibly();
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return baseFuture.await(timeoutMillis);
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return baseFuture.awaitUninterruptibly(timeoutMillis);
    }

    public boolean isCompleted() {
        return baseFuture.isCompleted();
    }

    public boolean isFailed() {
        return baseFuture.isFailed();
    }

    public FuturePut failed(BaseFuture origin) {
        return baseFuture.failed(origin);
    }

    public FuturePut failed(String failed, BaseFuture origin) {
        return baseFuture.failed(failed, origin);
    }

    public FuturePut failed(Throwable t) {
        return baseFuture.failed(t);
    }

    public FuturePut failed(String failed, Throwable t) {
        return baseFuture.failed(failed, t);
    }

    public FuturePut failed(String failed) {
        return baseFuture.failed(failed);
    }

    public String failedReason() {
        return baseFuture.failedReason();
    }

    public BaseFuture.FutureType type() {
        return baseFuture.type();
    }

    public FuturePut awaitListeners() throws InterruptedException {
        return baseFuture.awaitListeners();
    }

    public FuturePut awaitListenersUninterruptibly() {
        return baseFuture.awaitListenersUninterruptibly();
    }

    public FuturePut addListener(BaseFutureListener<? extends BaseFuture> listener) {
        return baseFuture.addListener(listener);
    }

    public FuturePut removeListener(BaseFutureListener<? extends BaseFuture> listener) {
        return baseFuture.removeListener(listener);
    }

    public FuturePut addCancel(Cancel cancelListener) {
        return baseFuture.addCancel(cancelListener);
    }

    public FuturePut removeCancel(Cancel cancelListener) {
        return baseFuture.removeCancel(cancelListener);
    }

    public void cancel() {
        baseFuture.cancel();
    }

    @Override
    protected PutResponse toValueAfterGet() {
        return new Result(baseFuture);
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
