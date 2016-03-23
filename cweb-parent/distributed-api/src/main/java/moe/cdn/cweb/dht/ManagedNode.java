package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;

import moe.cdn.cweb.security.CwebId;

public interface ManagedNode<T> {
    /**
     * Retrieves one value associated with the key.
     * 
     * @param key
     * @return
     */
    ListenableFuture<T> getOne(CwebId key);

    /**
     * Retrieves one value associated with a key and subkey on the same peer.
     * 
     * @param key
     * @param subKey
     * @return
     */
    ListenableFuture<T> getOne(CwebId key, CwebId subKey);

    /**
     * Gets all a collection of the values of all subkeys under a key.
     * 
     * @param key
     * @return
     */
    ListenableFuture<Collection<T>> getAll(CwebId key);

    /**
     * Insert a value into the bucket at specified key
     * 
     * @param key
     * @param t
     * @return
     */
    ListenableFuture<Boolean> add(CwebId key, T t);

    /**
     * Sets the bucket at specified key to contain a value.
     * 
     * @param key
     * @param t
     * @return
     */
    ListenableFuture<Boolean> put(CwebId key, T t);

}
