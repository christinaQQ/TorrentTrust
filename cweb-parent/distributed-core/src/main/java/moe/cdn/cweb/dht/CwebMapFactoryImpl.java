package moe.cdn.cweb.dht;

import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.security.CwebId;

/**
 * @author davix
 */
class CwebMapFactoryImpl<V extends Message> implements CwebMapFactory<V> {
    @Override
    public CwebMultiMap<V> create(ManagedDhtNode<V> dhtNode,
            Function<SecurityProtos.Hash, CwebId> keyReducer,
            BiPredicate<SecurityProtos.Hash, V> notCollision) {
        return new CwebMultiMapImpl<>(dhtNode, keyReducer, notCollision);
    }
}
