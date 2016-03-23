package moe.cdn.cweb.dht.internal;

import java.util.concurrent.Future;

import moe.cdn.cweb.dht.Shutdownable;
import moe.cdn.cweb.dht.internal.tomp2pcompat.BaseFutureAsListenableFuture;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;

public class PeerDhtShutdownable implements Shutdownable {
    private final PeerDHT peerDht;

    public PeerDhtShutdownable(PeerDHT peerDht) {
        this.peerDht = peerDht;
    }

    public PeerDHT getDhtInstance() {
        return peerDht;
    }

    @Override
    public Future<Void> shutdown() {
        return new BaseFutureAsListenableFuture<Void, BaseFuture>(peerDht.shutdown()) {
            @Override
            protected Void toValueAfterGet() {
                return null;
            }
        };
    }
}
