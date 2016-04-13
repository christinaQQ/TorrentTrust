package moe.cdn.cweb.dht.internal;

import java.util.Collection;

import com.google.protobuf.Message;

/**
 * @author davix
 */
public interface CwebGetResults<T extends Message> {
    Collection<T> all();

    T one();
}
