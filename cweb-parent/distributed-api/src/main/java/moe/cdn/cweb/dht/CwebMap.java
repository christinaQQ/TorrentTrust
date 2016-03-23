package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.security.CwebId;

import java.util.Collection;

/**
 * @author davix
 */
public interface CwebMap<V extends Message> extends AsyncMap<Hash, V> {
    ListenableFuture<V> get(CwebId key);

    ListenableFuture<Collection<V>> all(CwebId key);

    ListenableFuture<Boolean> put(CwebId key, V value);

    ListenableFuture<Boolean> add(CwebId key, V value);
}
