package moe.cdn.cweb.dht;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.inject.Inject;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.security.CwebId;

class CwebMultiMapImpl<V extends Message> implements CwebMultiMap<V> {

    private final ManagedDhtNode<V> collection;
    private final Function<Hash, CwebId> keyReducer;
    private final BiPredicate<Hash, V> notCollision; // FIXME: collisions?

    @Inject
    public CwebMultiMapImpl(ManagedDhtNode<V> collection,
            Function<Hash, CwebId> keyReducer,
            BiPredicate<Hash, V> notCollision) {
        this.collection = collection;
        this.keyReducer = keyReducer;
        this.notCollision = notCollision;
    }

    @Override
    public ListenableFuture<V> get(Hash key) {
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
    public ListenableFuture<Collection<V>> all(Hash key) {
        return collection.getAll(keyReducer.apply(key));
    }

    @Override
    public ListenableFuture<Boolean> containsKey(Hash key) {
        return Futures.transform(get(key), Objects::nonNull);
    }

    @Override
    public ListenableFuture<Boolean> put(Hash key, V value) {
        return collection.put(keyReducer.apply(key), value);
    }

    @Override
    public ListenableFuture<Boolean> put(CwebId key, V value) {
        return collection.put(key, value);
    }

    @Override
    public ListenableFuture<Boolean> add(Hash key, V value) {
        return collection.add(keyReducer.apply(key), value);
    }

    @Override
    public ListenableFuture<Boolean> add(CwebId key, V value) {
        return collection.add(key, value);
    }
}
