package moe.cdn.cweb.dht;

import java.util.Collection;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.security.CwebId;

/**
 * An {@link AsyncMultiMap} for proto types.
 *
 * @author davix
 */
public interface CwebMultiMap<V extends Message> extends AsyncMultiMap<Hash, V> {
    ListenableFuture<V> get(CwebId key);

    ListenableFuture<Collection<V>> all(CwebId key);

    ListenableFuture<Boolean> put(CwebId key, V value);

    ListenableFuture<Boolean> add(CwebId key, V value);
}
