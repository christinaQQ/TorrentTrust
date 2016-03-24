package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;

import moe.cdn.cweb.security.CwebId;

/**
 * Abstraction for operations on a DHT. This interface views the DHT as a multimap.
 *
 * @param <T> the type of values stored in the DHT
 * @author davix
 * @author cquanze
 */
public interface DhtNode<T> {
    /**
     * Retrieves one value associated with the key.
     *
     * @param key the key
     * @return a future for the value to which the specified key is mapped
     */
    ListenableFuture<T> getOne(CwebId key);

    /**
     * Retrieves one value associated with a key and a subkey.
     *
     * @param key    the key
     * @param subKey the subkey
     * @return a future for the value to which the specified key-subkey is mapped
     * @implSpec The {@code key} determines the location of the node that will store the value,
     * and the {@code subKey} is a key for the value stored on a node.
     */
    ListenableFuture<T> getOne(CwebId key, CwebId subKey);

    /**
     * Retrieves a collection of all values for the specified key.
     *
     * @param key the key
     * @return a future for all values to which the specified key is mapped
     * @implSpec This returns all subkeys under a key.
     */
    ListenableFuture<Collection<T>> getAll(CwebId key);

    /**
     * Adds the specified value into the bucket at the specified key.
     *
     * @param key the key
     * @param t   the value
     * @return a future containing {@code true} if the mapping is successfully added
     */
    ListenableFuture<Boolean> add(CwebId key, T t);

    /**
     * Sets the bucket at the specified key to contain the specified value.
     *
     * @param key the key
     * @param t   the value
     * @return a future containing {@code true} if the bucket is successfully set to the mapping
     */
    ListenableFuture<Boolean> put(CwebId key, T t);
}
