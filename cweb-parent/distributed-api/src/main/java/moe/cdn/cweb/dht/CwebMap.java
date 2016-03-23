package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.security.CwebId;

/**
 * @author davix
 */
public interface CwebMap<V extends Message> extends AsyncMap<Hash, V> {
    ListenableFuture<V> get(CwebId key);

    ListenableFuture<Collection<V>> all(CwebId key);

    ListenableFuture<Boolean> put(CwebId key, V value);

    ListenableFuture<Boolean> add(CwebId key, V value);
}
