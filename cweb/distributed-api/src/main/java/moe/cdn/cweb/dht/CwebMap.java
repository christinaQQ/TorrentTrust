package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import java.math.BigInteger;
import java.util.Collection;

public interface CwebMap<K extends Message, V extends Message> {
    ListenableFuture<V> get(K key);
    ListenableFuture<V> get(BigInteger key);
    ListenableFuture<Collection<V>> all(K key);
    ListenableFuture<Collection<V>> all(BigInteger key);
    ListenableFuture<Boolean> contains(K key);
    ListenableFuture<Boolean> put(K key, V data);
    ListenableFuture<Boolean> put(BigInteger key, V value);
}
