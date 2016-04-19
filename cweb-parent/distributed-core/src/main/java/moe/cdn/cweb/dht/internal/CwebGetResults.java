package moe.cdn.cweb.dht.internal;

import com.google.protobuf.Message;

import java.util.Collection;

/**
 * @author davix
 */
public interface CwebGetResults<T extends Message> {
    Collection<T> all();

    T one();
}
