package moe.cdn.cweb.dht;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.google.protobuf.Message;

public class CwebMapImpl<K extends Message, V extends Message> implements CwebMap<K, V> {

    private final CwebCollection<V> collection;
    private final Function<K, BigInteger> keyReducer;
    private final BiPredicate<K, V> valueFilter;

    public CwebMapImpl(CwebCollection<V> collection,
            Function<K, BigInteger> keyReducer,
            BiPredicate<K, V> valueFilter) {
        this.collection = collection;
        this.keyReducer = keyReducer;
        this.valueFilter = valueFilter;
    }

    @Override
    public CwebFutureGet<V> get(K key) {
        return new CwebMapFutureGetImpl<V>(collection.get(keyReducer.apply(key)),
                value -> valueFilter.test(key, value));
    }

    @Override
    public CwebFutureGet<Boolean> contains(K key) {
        return new FutureContains(get(key));
    }

    @Override
    public CwebFuturePut put(K key, V value) {
        return collection.put(keyReducer.apply(key), value);
    }

    @Override
    public CwebFuturePut put(BigInteger key, V value) {
        return collection.put(key, value);
    }

    @Override
    public CwebFutureGet<V> get(BigInteger key) {
        return collection.get(key);
    }

    private static class FutureContains implements CwebFutureGet<Boolean> {

        private final CwebFutureGet<?> futureGet;

        FutureContains(CwebFutureGet<?> futureGet) {
            this.futureGet = futureGet;
        }

        @Override
        public Collection<Boolean> getAll() throws InterruptedException {
            return Collections.singleton(!futureGet.getAll().isEmpty());
        }

        @Override
        public Collection<Boolean> getAll(long timeout, TimeUnit units)
                throws InterruptedException, TimeoutException {
            return Collections.singleton(!futureGet.getAll(timeout, units).isEmpty());
        }

    }
}
