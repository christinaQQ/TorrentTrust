package moe.cdn.cweb.dht;

import java.math.BigInteger;

import com.google.protobuf.Message;

public interface CwebCollection<T extends Message> {
    /**
     * Returns a collection containing things in this collection under a certain
     * content key
     * 
     */
    CwebFutureGet<T> get(BigInteger key);

    /**
     * Puts a value into this collection under the key provided by key
     * 
     * @param key
     * @param value
     * @return
     */
    CwebFuturePut put(BigInteger key, T value);
}
