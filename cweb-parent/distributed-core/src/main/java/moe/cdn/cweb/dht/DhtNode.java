package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import moe.cdn.cweb.security.utils.CwebId;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author davix
 */
public interface DhtNode<T extends Message> {
    ListenableFuture<T> getOne(CwebId key);

    ListenableFuture<T> getOne(CwebId key, CwebId subKey);

    ListenableFuture<Collection<T>> getAll(CwebId key);

    ListenableFuture<Boolean> add(CwebId key, T t);

    ListenableFuture<Boolean> put(CwebId key, T t);

    Future<Void> shutdown();
}
