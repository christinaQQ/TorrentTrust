package moe.cdn.cweb.dht.internal.tomp2pcompat;

import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.dht.DHTBuilder;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.futures.*;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;

import java.util.Map;

/**
 * @author davix
 */
public class FutureGetWrapper extends BaseFutureAsListenableFuture<GetResponse, FutureGet> {

    public FutureGetWrapper(FutureGet futureGet) {
        super(futureGet);
    }

    /**
     * @return A reference to the builder that contains the data we were looking
     * for
     */
    public DHTBuilder<?> builder() {
        return baseFuture.builder();
    }

    /**
     * Returns back those futures that are still running. If 6 storage futures
     * are started at the same time and 5 of them finish, and we specified that
     * we are fine if 5 finishes, then futureDHT returns success. However, the
     * future that may still be running is the one that stores the content to
     * the closest peer. For testing this is not acceptable, thus after waiting
     * for futureDHT, one needs to wait for the running futures as well.
     *
     * @return A future that finishes if all running futures are finished.
     */
    public FutureForkJoin<FutureResponse> futureRequests() {
        return baseFuture.futureRequests();
    }

    /**
     * Adds all requests that have been created for the DHT operations. Those
     * were created after the routing process.
     *
     * @param futureResponse The futurRepsonse that has been created
     */
    public FutureGet addRequests(FutureResponse futureResponse) {
        return baseFuture.addRequests(futureResponse);
    }

    /**
     * Adds a listener to the response future and releases all aquired channels
     * in channel creator.
     *
     * @param channelCreator The channel creator that will be shutdown and all
     *                       connections will be closed
     */
    public void addFutureDHTReleaseListener(ChannelCreator channelCreator) {
        baseFuture.addFutureDHTReleaseListener(channelCreator);
    }

    /**
     * Returns the future object that was used for the routing. Before the
     * FutureDHT is used, FutureRouting has to be completed successfully.
     *
     * @return The future object during the previous routing, or null if routing
     * failed completely.
     */
    public FutureRouting futureRouting() {
        return baseFuture.futureRouting();
    }

    /**
     * Sets the future object that was used for the routing. Before the
     * FutureDHT is used, FutureRouting has to be completed successfully.
     *
     * @param futureRouting The future object to set
     */
    public void futureRouting(FutureRouting futureRouting) {
        baseFuture.futureRouting(futureRouting);
    }

    public FutureDone<Void> futuresCompleted() {
        return baseFuture.futuresCompleted();
    }

    public FutureGet await() throws InterruptedException {
        return baseFuture.await();
    }

    public FutureGet awaitUninterruptibly() {
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

    public boolean isSuccess() {
        return baseFuture.isSuccess();
    }

    public boolean isFailed() {
        return baseFuture.isFailed();
    }

    public FutureGet failed(BaseFuture origin) {
        return baseFuture.failed(origin);
    }

    public FutureGet failed(String failed, BaseFuture origin) {
        return baseFuture.failed(failed, origin);
    }

    public FutureGet failed(Throwable t) {
        return baseFuture.failed(t);
    }

    public FutureGet failed(String failed, Throwable t) {
        return baseFuture.failed(failed, t);
    }

    public FutureGet failed(String failed) {
        return baseFuture.failed(failed);
    }

    public String failedReason() {
        return baseFuture.failedReason();
    }

    public BaseFuture.FutureType type() {
        return baseFuture.type();
    }

    public FutureGet awaitListeners() throws InterruptedException {
        return baseFuture.awaitListeners();
    }

    public FutureGet awaitListenersUninterruptibly() {
        return baseFuture.awaitListenersUninterruptibly();
    }

    public FutureGet addListener(BaseFutureListener<? extends BaseFuture> listener) {
        return baseFuture.addListener(listener);
    }

    public FutureGet removeListener(BaseFutureListener<? extends BaseFuture> listener) {
        return baseFuture.removeListener(listener);
    }

    public FutureGet addCancel(Cancel cancelListener) {
        return baseFuture.addCancel(cancelListener);
    }

    public FutureGet removeCancel(Cancel cancelListener) {
        return baseFuture.removeCancel(cancelListener);
    }

    public void cancel() {
        baseFuture.cancel();
    }

    @Override
    protected Result toValueAfterGet() {
        return new Result(baseFuture);
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
        public void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData,
                                 Map<PeerAddress, DigestResult> rawDigest,
                                 Map<PeerAddress, Byte> rawStatus,
                                 FutureDone<Void> futuresCompleted) {
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
