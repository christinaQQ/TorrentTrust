package moe.cdn.cweb.dht;

import java.math.BigInteger;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

/**
 * @author davix
 */
public class CwebCollectionImpl<T extends Message> implements CwebCollection<T> {
    // TODO: Examine protection
    // http://lists.tomp2p.net/pipermail/users/2013-July/000266.html

    private final PeerDHT peer;
    private final Number160 domainKey;
    private final Parser<T> messageParser;

    public CwebCollectionImpl(PeerDHT peer, Number160 domainKey, Parser<T> messageParser) {
        this.peer = peer;
        this.domainKey = domainKey;
        this.messageParser = messageParser;
    }

    @Override
    public CwebFutureGetImpl<T> get(BigInteger key) {
        return new CwebFutureGetImpl<>(startGetData(new Number160(key.toByteArray())),
                messageParser);
    }

    @Override
    public CwebFuturePutImpl put(BigInteger key, T t) {
        return new CwebFuturePutImpl(startAddData(new Number160(key.toByteArray()), t));
    }

    /**
     * Starts a computation to get all data under a certain content key
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData(Number160 locationKey) {
        return peer.get(locationKey).domainKey(domainKey).all().start();
    }

    /**
     * Starts a computation to add new data.
     *
     * @param t the data
     * @return a {@link FuturePut} computation
     */
    protected FuturePut startAddData(Number160 locationKey, T t) {
        return peer.add(locationKey).domainKey(domainKey).data(new Data(t.toByteArray())).start();
    }
}
