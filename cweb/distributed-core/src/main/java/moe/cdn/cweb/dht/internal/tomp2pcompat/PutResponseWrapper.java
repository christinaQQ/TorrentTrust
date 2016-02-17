package moe.cdn.cweb.dht.internal.tomp2pcompat;

import net.tomp2p.futures.FutureDone;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;

import java.util.Map;

public class PutResponseWrapper implements PutResponse {
    private final PutResponse underlying;

    public PutResponseWrapper(PutResponse underlying) {
        this.underlying = underlying;
    }

    @Override
    public void storedKeys(Map<PeerAddress, Map<Number640, Byte>> rawResult, FutureDone<Void>
            futuresCompleted) {
        underlying.storedKeys(rawResult, futuresCompleted);
    }

    @Override
    public double avgStoredKeys() {
        return underlying.avgStoredKeys();
    }

    @Override
    public Map<PeerAddress, Map<Number640, Byte>> rawResult() {
        return underlying.rawResult();
    }

    @Override
    public boolean isMinReached() {
        return underlying.isMinReached();
    }

    /**
     * @apiNote returns a frequency map of the stored {@link Number640}s
     */
    @Override
    public Map<Number640, Integer> result() {
        return underlying.result();
    }

    @Override
    public boolean isSuccess() {
        return underlying.isSuccess();
    }

    @Override
    public boolean isSuccessPartially() {
        return underlying.isSuccessPartially();
    }
}
