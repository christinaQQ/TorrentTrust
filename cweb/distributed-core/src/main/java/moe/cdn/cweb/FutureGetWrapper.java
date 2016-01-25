package moe.cdn.cweb;

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
public class FutureGetWrapper {
    private final FutureGet underlying;

    public FutureGetWrapper(FutureGet underlying) {
        this.underlying = underlying;
    }

    public void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData, Map<PeerAddress,
            DigestResult> rawDigest, Map<PeerAddress, Byte> rawStatus, FutureDone<Void>
            futuresCompleted) {
        underlying.receivedData(rawData, rawDigest, rawStatus, futuresCompleted);
    }

    public FutureGet addCancel(Cancel cancelListener) {
        return underlying.addCancel(cancelListener);
    }

    public void cancel() {
        underlying.cancel();
    }

    public Map<Number640, Data> dataMap() {
        return underlying.dataMap();
    }

    public FutureGet addRequests(FutureResponse futureResponse) {
        return underlying.addRequests(futureResponse);
    }

    public FutureDone<Void> futuresCompleted() {
        return underlying.futuresCompleted();
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

    public boolean isSuccess() {
        return underlying.isSuccess();
    }

    public void futureRouting(FutureRouting futureRouting) {
        underlying.futureRouting(futureRouting);
    }

    public FutureGet failed(String failed) {
        return underlying.failed(failed);
    }

    public Map<PeerAddress, Map<Number640, Data>> rawData() {
        return underlying.rawData();
    }

    public boolean awaitUninterruptibly(long timeoutMillis) {
        return underlying.awaitUninterruptibly(timeoutMillis);
    }

    public boolean await(long timeoutMillis) throws InterruptedException {
        return underlying.await(timeoutMillis);
    }

    public FutureGet failed(String failed, Throwable t) {
        return underlying.failed(failed, t);
    }

    public FutureGet awaitListenersUninterruptibly() {
        return underlying.awaitListenersUninterruptibly();
    }

    public FutureGet removeListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.removeListener(listener);
    }

    public FutureRouting futureRouting() {
        return underlying.futureRouting();
    }

    public FutureGet failed(Throwable t) {
        return underlying.failed(t);
    }

    public Data data() {
        return underlying.data();
    }

    public boolean isMinReached() {
        return underlying.isMinReached();
    }

    public DigestResult digest() {
        return underlying.digest();
    }

    public boolean isCompleted() {
        return underlying.isCompleted();
    }

    public Map<PeerAddress, Byte> rawStatus() {
        return underlying.rawStatus();
    }

    public Map<PeerAddress, DigestResult> rawDigest() {
        return underlying.rawDigest();
    }

    public FutureGet await() throws InterruptedException {
        return underlying.await();
    }

    public FutureGet awaitListeners() throws InterruptedException {
        return underlying.awaitListeners();
    }

    public FutureGet addListener(BaseFutureListener<? extends BaseFuture> listener) {
        return underlying.addListener(listener);
    }

    public FutureGet awaitUninterruptibly() {
        return underlying.awaitUninterruptibly();
    }

    public void addFutureDHTReleaseListener(ChannelCreator channelCreator) {
        underlying.addFutureDHTReleaseListener(channelCreator);
    }

    public boolean isEmpty() {
        return underlying.isEmpty();
    }

    public DHTBuilder<?> builder() {
        return underlying.builder();
    }

    public FutureForkJoin<FutureResponse> futureRequests() {
        return underlying.futureRequests();
    }

    public BaseFuture.FutureType type() {
        return underlying.type();
    }

    public String failedReason() {
        return underlying.failedReason();
    }

    public FutureGet removeCancel(Cancel cancelListener) {
        return underlying.removeCancel(cancelListener);
    }

}
