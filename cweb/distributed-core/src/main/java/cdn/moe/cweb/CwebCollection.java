package cdn.moe.cweb;

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
public class CwebCollection<T extends Message> {
    private final PeerDHT peer;
    private final Number160 locationKey;
    private final Number160 domainKey;
    private final Parser<T> messageParser;

    public CwebCollection(PeerDHT peer, Number160 locationKey, Number160 domainKey, Parser<T> messageParser) {
        this.peer = peer;
        this.locationKey = locationKey;
        this.domainKey = domainKey;
        this.messageParser = messageParser;
    }

    public CwebFutureGet<T> get() {
        return new CwebFutureGet<>(startGetData(), messageParser);
    }

    public FuturePut add(T t) {
        return startAddData(t);
    }
    /**
     * Starts a computation to get all data.
     *
     * @return a {@link FutureGet} computation
     */
    protected FutureGet startGetData() {
        return peer.get(locationKey).domainKey(domainKey).all().start();
    }

    /**
     * Starts a computation to add new data.
     *
     * @param t the data
     * @return a {@link FutureGet} computation
     */
    protected FuturePut startAddData(T t) {
        return peer.add(locationKey).domainKey(domainKey).data(new Data(t.toByteArray())).start();
    }

}
