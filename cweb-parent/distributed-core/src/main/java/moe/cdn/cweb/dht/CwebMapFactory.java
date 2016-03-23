package moe.cdn.cweb.dht;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.security.CwebId;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author davix
 */
public interface CwebMapFactory<V extends Message> {
    CwebMap<V> create(DhtNode<V> dhtNode,
                      Function<SecurityProtos.Hash, CwebId> keyReducer,
                      BiPredicate<SecurityProtos.Hash, V> notCollision);
}
