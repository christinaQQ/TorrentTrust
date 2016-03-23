package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.security.CwebId;
import java.util.Collection;
import java.util.concurrent.Future;

public interface AsyncMap<K extends Message, V extends Message> {
    ListenableFuture<V> get(K key);

    ListenableFuture<Collection<V>> all(K key);

    ListenableFuture<Boolean> contains(K key);

    ListenableFuture<Boolean> put(K key, V value);

    ListenableFuture<Boolean> add(K key, V value);

    Future<Void> shutdown();
}
