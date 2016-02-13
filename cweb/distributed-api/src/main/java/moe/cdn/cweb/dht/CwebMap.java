package moe.cdn.cweb.dht;

import com.google.protobuf.Message;

public interface CwebMap<K extends Message, V extends Message> extends CwebCollection<V> {
    /**
     * Get an object from the CwebMap
     * 
     * @return
     */
    CwebFutureGet<V> get(K key);

    /**
     * Check if an object is contained in the CwebMap
     * 
     * @return
     */
    CwebFutureGet<Boolean> contains(K key);

    /**
     * Insert an object into the CwebMap
     * 
     * @param data
     * @return
     */
    CwebFuturePut put(K key, V data);
}
