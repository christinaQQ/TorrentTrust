package moe.cdn.cweb.dht.internal.tomp2pcompat;

import java.util.Map;

import net.tomp2p.futures.FutureDone;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;

/**
 * @author davix
 */
public class GetResponseWrapper implements GetResponse {
    private final GetResponse underlying;

    public GetResponseWrapper(GetResponse underlying) {
        this.underlying = underlying;
    }

    @Override
    public Map<PeerAddress, Map<Number640, Data>> rawData() {
        return underlying.rawData();
    }

    @Override
    public Map<PeerAddress, DigestResult> rawDigest() {
        return underlying.rawDigest();
    }

    @Override
    public Map<Number640, Data> dataMap() {
        return underlying.dataMap();
    }

    @Override
    public Data data() {
        return underlying.data();
    }

    @Override
    public DigestResult digest() {
        return underlying.digest();
    }

    @Override
    public Map<PeerAddress, Byte> rawStatus() {
        return underlying.rawStatus();
    }

    @Override
    public void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData,
            Map<PeerAddress, DigestResult> rawDigest,
            Map<PeerAddress, Byte> rawStatus,
            FutureDone<Void> futuresCompleted) {
        underlying.receivedData(rawData, rawDigest, rawStatus, futuresCompleted);
    }

    @Override
    public boolean isMinReached() {
        return underlying.isMinReached();
    }

    @Override
    public boolean isEmpty() {
        return underlying.isEmpty();
    }
}
