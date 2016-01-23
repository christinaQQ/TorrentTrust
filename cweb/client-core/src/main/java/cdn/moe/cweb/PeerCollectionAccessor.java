package cdn.moe.cweb;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.List;

/**
 * @author davix
 */
public class PeerCollectionAccessor<T> {
    final private PeerDHT peer;
    private final Number160 locationKey;
    private final Number160 domainKey;

    PeerCollectionAccessor(PeerDHT peer, Number160 locationKey, Number160 domainKey) {
        this.peer = peer;
        this.locationKey = locationKey;
        this.domainKey = domainKey;
    }

    public FutureGet get() {
        return peer.get(locationKey).domainKey(domainKey).all().start();
    }

    public FuturePut add(T t) throws IOException {
        return peer.add(locationKey).domainKey(domainKey).data(new Data(t)).start();
    }
}
