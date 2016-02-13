package moe.cdn.cweb.dht;

import net.tomp2p.connection.ChannelCreator;
import net.tomp2p.dht.DHTBuilder;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.futures.*;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author davix
 */
class FutureGetWrapper implements Future<ResponseData> {
    private final FutureGet underlying;
    private volatile boolean cancelled;

    public FutureGetWrapper(FutureGet underlying) {
        this.underlying = underlying;
    }

    public DHTBuilder<?> builder() {
        return underlying.builder();
    }

    public FutureForkJoin<FutureResponse> futureRequests() {
        return underlying.futureRequests();
    }

    public FutureGet addRequests(FutureResponse futureResponse) {
        return underlying.addRequests(futureResponse);
    }

    public void addFutureDHTReleaseListener(ChannelCreator channelCreator) {
        underlying.addFutureDHTReleaseListener(channelCreator);
    }

    public FutureRouting futureRouting() {
        return underlying.futureRouting();
    }

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
    public ResponseData get() throws InterruptedException {
        return new Result(underlying.await());
    }

    @Override
    public ResponseData get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        boolean ok = underlying.await(unit.toMillis(timeout));
        if (!ok) {
            throw new TimeoutException();
        }
        return new Result(underlying);
    }

    /**
     * @author davix
     */
    public static class Result implements ResponseData {
        private final FutureGet futureGet;

        public Result(FutureGet futureGet) {
            this.futureGet = futureGet;
        }

        @Override
        public Map<PeerAddress, Map<Number640, net.tomp2p.storage.Data>> rawData() {
            return futureGet.rawData();
        }

        @Override
        public Map<PeerAddress, DigestResult> rawDigest() {
            return futureGet.rawDigest();
        }

        @Override
        public Map<Number640, net.tomp2p.storage.Data> dataMap() {
            return futureGet.dataMap();
        }

        @Override
        public net.tomp2p.storage.Data data() {
            return futureGet.data();
        }

        @Override
        public DigestResult digest() {
            return futureGet.digest();
        }

        @Override
        public Map<PeerAddress, Byte> rawStatus() {
            return futureGet.rawStatus();
        }

        @Override
        public void receivedData(Map<PeerAddress, Map<Number640, net.tomp2p.storage.Data>> rawData, Map<PeerAddress,
                DigestResult> rawDigest, Map<PeerAddress, Byte> rawStatus, FutureDone<Void>
                                         futuresCompleted) {
            futureGet.receivedData(rawData, rawDigest, rawStatus, futuresCompleted);
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
