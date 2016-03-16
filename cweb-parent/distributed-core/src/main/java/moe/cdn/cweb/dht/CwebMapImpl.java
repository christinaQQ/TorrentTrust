package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import javax.inject.Inject;
import moe.cdn.cweb.security.utils.CwebId;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CwebMapImpl<K extends Message, V extends Message> implements CwebMap<K, V> {

    private final DhtNode<V> collection;
    private final Function<K, CwebId> keyReducer;
    private final BiPredicate<K, V> notCollision; // FIXME: collisions?

    @Inject
    public CwebMapImpl(DhtNode<V> collection,
            Function<K, CwebId> keyReducer,
            BiPredicate<K, V> notCollision) {
        this.collection = collection;
        this.keyReducer = keyReducer;
        this.notCollision = notCollision;
    }

    @Override
    public ListenableFuture<V> get(K key) {
        return get(keyReducer.apply(key));
    }

    @Override
    public ListenableFuture<V> get(CwebId key) {
        return collection.getOne(key);
    }

    @Override
    public ListenableFuture<Collection<V>> all(CwebId key) {
        return collection.getAll(key);
    }

    @Override
    public ListenableFuture<Collection<V>> all(K key) {
        return collection.getAll(keyReducer.apply(key));
    }

    @Override
    public ListenableFuture<Boolean> contains(K key) {
        return Futures.transform(get(key), Objects::nonNull);
    }

    @Override
    public ListenableFuture<Boolean> put(K key, V value) {
        return collection.put(keyReducer.apply(key), value);
    }

    @Override
    public ListenableFuture<Boolean> put(CwebId key, V value) {
        return collection.put(key, value);
    }

    @Override
    public ListenableFuture<Boolean> add(K key, V value) {
        return collection.add(keyReducer.apply(key), value);
    }

    @Override
    public ListenableFuture<Boolean> add(CwebId key, V value) {
        return collection.add(key, value);
    }
}
