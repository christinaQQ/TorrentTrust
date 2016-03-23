package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

public interface AsyncMap<K extends Message, V extends Message> {
    ListenableFuture<V> get(K key);

    ListenableFuture<Collection<V>> all(K key);

    ListenableFuture<Boolean> contains(K key);

    ListenableFuture<Boolean> put(K key, V value);

    ListenableFuture<Boolean> add(K key, V value);
}
