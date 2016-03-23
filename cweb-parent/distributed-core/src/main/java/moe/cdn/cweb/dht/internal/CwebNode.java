package moe.cdn.cweb.dht.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import moe.cdn.cweb.dht.Shutdownable;
import moe.cdn.cweb.dht.internal.tomp2pcompat.FutureGetWrapper;
import moe.cdn.cweb.dht.internal.tomp2pcompat.FuturePutWrapper;
import moe.cdn.cweb.dht.internal.tomp2pcompat.GetResponse;
import moe.cdn.cweb.dht.internal.tomp2pcompat.PutResponse;
import moe.cdn.cweb.dht.util.Number160s;
import moe.cdn.cweb.security.CwebId;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * Entry point for DHT operations. This is a wrapper around a {@link PeerDHT}.
 *
 * @author davix
 */
public class CwebNode<T extends Message> implements Shutdownable {
    // FIXME tomp2p workaround
    private static final Executor EXECUTOR = ForkJoinPool.commonPool();
    private final PeerDhtShutdownable self;
    private final Number160 domainKey;
    private final Parser<T> messageParser;

    // TODO: Examine protection
    // http://lists.tomp2p.net/pipermail/users/2013-July/000266.html

    public CwebNode(PeerDhtShutdownable self, Number160 domainKey, Parser<T> messageParser) {
        this.self = self;
        this.domainKey = domainKey;
        this.messageParser = messageParser;
    }

    public ListenableFuture<CwebGetResults<T>> all(CwebId key) {
        FutureGetWrapper f = new FutureGetWrapper(startAllData(Number160s.fromCwebId(key)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>) r -> new CwebGetResultsImpl<T>(
                        messageParser, r),
                EXECUTOR);
    }

    public ListenableFuture<CwebGetResults<T>> get(CwebId key) {
        FutureGetWrapper f = new FutureGetWrapper(startGetData(Number160s.fromCwebId(key)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>) r -> new CwebGetResultsImpl<T>(
                        messageParser, r),
                EXECUTOR);
    }

    public ListenableFuture<CwebGetResults<T>> get(CwebId key, CwebId subKey) {
        FutureGetWrapper f = new FutureGetWrapper(
                startGetData(Number160s.fromCwebId(key), Number160s.fromCwebId(subKey)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>) r -> new CwebGetResultsImpl<T>(
                        messageParser, r),
                EXECUTOR);
    }

    public ListenableFuture<CwebPutResults> add(CwebId key, T t) {
        FuturePutWrapper f = new FuturePutWrapper(startAddData(Number160s.fromCwebId(key), t));
        return Futures.transform(f,
                (Function<PutResponse, CwebPutResultsImpl>) CwebPutResultsImpl::new, EXECUTOR);
    }

    public ListenableFuture<CwebPutResults> put(CwebId key, T t) {
        FuturePutWrapper f = new FuturePutWrapper(startPutData(Number160s.fromCwebId(key), t));
        return Futures.transform(f,
                (Function<PutResponse, CwebPutResultsImpl>) CwebPutResultsImpl::new, EXECUTOR);
    }

    /**
     * Starts a computation to get all data under a certain content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startAllData(Number160 locationKey) {
        return self.getDhtInstance().get(locationKey).domainKey(domainKey).all().start();
    }

    /**
     * Starts a computation to get data under a specified content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData(Number160 locationKey, Number160 contentKey) {
        return self.getDhtInstance().get(locationKey).domainKey(domainKey).contentKey(contentKey)
                .start();
    }

    /**
     * Starts a computation to get data under the default content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData(Number160 locationKey) {
        return self.getDhtInstance().get(locationKey).domainKey(domainKey).start();
    }

    /**
     * Starts a computation to add new data.
     *
     * @param t the data
     * @return a {@link FuturePut} computation
     */
    protected FuturePut startAddData(Number160 locationKey, T t) {
        return self.getDhtInstance().add(locationKey).domainKey(domainKey)
                .data(new Data(t.toByteArray())).start();
    }

    /**
     * Starts a computation to put new data.
     *
     * @param t the data
     * @return a {@link FuturePut} computation
     */
    protected FuturePut startPutData(Number160 locationKey, T t) {
        return self.getDhtInstance().put(locationKey).domainKey(domainKey)
                .data(new Data(t.toByteArray())).start();
    }

    public Future<Void> shutdown() {
        return self.shutdown();
    }
}
