package moe.cdn.cweb.dht.internal;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import moe.cdn.cweb.dht.internal.tomp2pcompat.FutureGetWrapper;
import moe.cdn.cweb.dht.internal.tomp2pcompat.FuturePutWrapper;
import moe.cdn.cweb.dht.internal.tomp2pcompat.GetResponse;
import moe.cdn.cweb.dht.internal.tomp2pcompat.PutResponse;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.math.BigInteger;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Entry point for DHT operations.
 *
 * @author davix
 */
public class CwebNode<T extends Message> {
    private static final Executor EXECUTOR = ForkJoinPool.commonPool(); // FIXME tomp2p workaround
    private final PeerDHT self;
    private final Number160 domainKey;
    private final Parser<T> messageParser;

    // TODO: Examine protection
    // http://lists.tomp2p.net/pipermail/users/2013-July/000266.html

    @Inject
    public CwebNode(PeerDHT self, Number160 domainKey, Parser<T> messageParser) {
        this.self = self;
        this.domainKey = domainKey;
        this.messageParser = messageParser;
    }

    private static Number160 toNumber160(BigInteger key) {
        // FIXME: validate size of key?
        return new Number160(key.toByteArray());
    }

    public ListenableFuture<CwebGetResults<T>> all(BigInteger key) {
        FutureGetWrapper f = new FutureGetWrapper(startAllData(toNumber160(key)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>)
                        r -> new CwebGetResultsImpl<T>(messageParser, r), EXECUTOR);
    }

    public ListenableFuture<CwebGetResults<T>> get(BigInteger key) {
        FutureGetWrapper f = new FutureGetWrapper(startGetData(toNumber160(key)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>)
                        r -> new CwebGetResultsImpl<T>(messageParser, r), EXECUTOR);
    }

    public ListenableFuture<CwebGetResults<T>> get(BigInteger key, BigInteger subKey) {
        FutureGetWrapper f = new FutureGetWrapper(
                startGetData(toNumber160(key),
                        toNumber160(subKey)));
        return Futures.transform(f,
                (Function<GetResponse, CwebGetResultsImpl<T>>)
                        r -> new CwebGetResultsImpl<T>(messageParser, r), EXECUTOR);
    }

    public ListenableFuture<CwebPutResults> add(BigInteger key, T t) {
        FuturePutWrapper f = new FuturePutWrapper(
                startAddData(toNumber160(key), t));
        return Futures.transform(f,
                (Function<PutResponse, CwebPutResultsImpl>) CwebPutResultsImpl::new, EXECUTOR);
    }

    public ListenableFuture<CwebPutResults> put(BigInteger key, T t) {
        FuturePutWrapper f = new FuturePutWrapper(
                startPutData(toNumber160(key), t));
        return Futures.transform(f,
                (Function<PutResponse, CwebPutResultsImpl>) CwebPutResultsImpl::new, EXECUTOR);
    }

    /**
     * Starts a computation to get all data under a certain content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startAllData(Number160 locationKey) {
        return self.get(locationKey).domainKey(domainKey).all().start();
    }

    /**
     * Starts a computation to get data under a specified content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData(Number160 locationKey, Number160 contentKey) {
        return self.get(locationKey).domainKey(domainKey).contentKey(contentKey).start();
    }

    /**
     * Starts a computation to get data under the default content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData(Number160 locationKey) {
        return self.get(locationKey).domainKey(domainKey).start();
    }

    /**
     * Starts a computation to add new data.
     *
     * @param t the data
     * @return a {@link FuturePut} computation
     */
    protected FuturePut startAddData(Number160 locationKey, T t) {
        return self.add(locationKey).domainKey(domainKey).data(new Data(t.toByteArray())).start();
    }

    /**
     * Starts a computation to put new data.
     *
     * @param t the data
     * @return a {@link FuturePut} computation
     */
    protected FuturePut startPutData(Number160 locationKey, T t) {
        return self.put(locationKey).domainKey(domainKey).data(new Data(t.toByteArray())).start();
    }

}
