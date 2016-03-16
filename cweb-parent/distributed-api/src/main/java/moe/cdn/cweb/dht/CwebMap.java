package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.security.utils.CwebId;
import java.util.Collection;

public interface CwebMap<K extends Message, V extends Message> {
    ListenableFuture<V> get(K key);

    ListenableFuture<V> get(CwebId key);

    ListenableFuture<Collection<V>> all(K key);

    ListenableFuture<Collection<V>> all(CwebId key);

    ListenableFuture<Boolean> contains(K key);

    ListenableFuture<Boolean> put(K key, V value);

    ListenableFuture<Boolean> put(CwebId key, V value);

    ListenableFuture<Boolean> add(K key, V value);

    ListenableFuture<Boolean> add(CwebId key, V value);
}
