package moe.cdn.cweb.dht;

import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.security.CwebId;

/**
 * @author davix
 */
public interface CwebMapFactory<V extends Message> {
    CwebMultiMap<V> create(ManagedDhtNode<V> dhtNode,
            Function<SecurityProtos.Hash, CwebId> keyReducer,
            BiPredicate<SecurityProtos.Hash, V> notCollision);
}
