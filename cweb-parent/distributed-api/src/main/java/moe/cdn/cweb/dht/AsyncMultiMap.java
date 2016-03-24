package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * A multimap that backed by asynchronous operations.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author davix
 */
public interface AsyncMultiMap<K, V> {
    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key the key
     * @return a future for the value
     */
    ListenableFuture<V> get(K key);

    /**
     * Returns all values to which the specified key is mapped.
     *
     * @param key the key
     * @return a future for all values to which the specified key is mapped
     */
    ListenableFuture<Collection<V>> all(K key);

    /**
     * Returns true if the specified key exists in the map
     *
     * @param key the key
     * @return a future containing {@code true} if the specified key exists.
     */
    ListenableFuture<Boolean> containsKey(K key);

    /**
     * Sets the bucket at the specified key to contain the specified value.
     *
     * @param key   the key
     * @param value the value
     * @return a future containing {@code true} if the mapping is successfully set
     */
    ListenableFuture<Boolean> put(K key, V value);

    /**
     * Adds the specified value to the bucket at the specified key.
     *
     * @param key   the key
     * @param value the value
     * @return a future containing {@code true} if the mapping is successfully added
     */
    ListenableFuture<Boolean> add(K key, V value);
}
