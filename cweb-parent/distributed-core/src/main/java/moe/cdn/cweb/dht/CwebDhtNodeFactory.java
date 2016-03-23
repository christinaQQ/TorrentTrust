package moe.cdn.cweb.dht;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import moe.cdn.cweb.dht.internal.CwebGetResults;
import moe.cdn.cweb.dht.internal.CwebNode;
import moe.cdn.cweb.dht.internal.CwebPutResults;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.peers.Number160;

import moe.cdn.cweb.security.CwebId;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author davix
 */
class CwebDhtNodeFactory implements DhtNodeFactory {

    @Override
    public <T extends Message> DhtNode<T> create(PeerDHT self, String domainKey,
                                                 Parser<T> messageParser) {
        return new AsCwebDhtNode<>(
                new CwebNode<>(self, Number160.createHash(domainKey), messageParser));
    }

    private static class AsCwebDhtNode<T extends Message> implements DhtNode<T> {
        private final CwebNode<T> cwebNode;

        public AsCwebDhtNode(CwebNode<T> cwebNode) {
            this.cwebNode = cwebNode;
        }

        @Override
        public ListenableFuture<T> getOne(CwebId key) {
            return Futures.transform(cwebNode.get(key),
                    (Function<? super CwebGetResults<T>, ? extends T>) CwebGetResults::one);
        }

        @Override
        public ListenableFuture<T> getOne(CwebId key, CwebId subKey) {
            return Futures.transform(cwebNode.get(key, subKey),
                    (Function<? super CwebGetResults<T>, ? extends T>) CwebGetResults::one);
        }

        @Override
        public ListenableFuture<Collection<T>> getAll(CwebId key) {
            return Futures.transform(cwebNode.all(key),
                    (Function<? super CwebGetResults<T>, ? extends Collection<T>>)
                            CwebGetResults::all);
        }

        @Override
        public ListenableFuture<Boolean> add(CwebId key, T t) {
            return Futures.transform(cwebNode.add(key, t), CwebPutResults::ok);
        }

        @Override
        public ListenableFuture<Boolean> put(CwebId key, T t) {
            return Futures.transform(cwebNode.put(key, t), CwebPutResults::ok);
        }

        @Override
        public Future<Void> shutdown() {
            return cwebNode.shutdown();
        }
    }
}
