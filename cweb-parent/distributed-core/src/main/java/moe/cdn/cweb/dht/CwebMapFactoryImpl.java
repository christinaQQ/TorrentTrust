package moe.cdn.cweb.dht;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.security.CwebId;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author davix
 */
class CwebMapFactoryImpl<V extends Message> implements CwebMapFactory<V> {
    @Override
    public CwebMap<V> create(DhtNode<V> dhtNode,
                             Function<SecurityProtos.Hash, CwebId> keyReducer,
                             BiPredicate<SecurityProtos.Hash, V> notCollision) {
        return new CwebMapImpl<>(dhtNode, keyReducer, notCollision);
    }
}
