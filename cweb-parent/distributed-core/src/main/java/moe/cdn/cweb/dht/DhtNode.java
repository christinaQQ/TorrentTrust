package moe.cdn.cweb.dht;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Message;

import java.math.BigInteger;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author davix
 */
public interface DhtNode<T extends Message> {
    ListenableFuture<T> getOne(BigInteger key);

    ListenableFuture<T> getOne(BigInteger key, BigInteger subKey);

    ListenableFuture<Collection<T>> getAll(BigInteger key);

    ListenableFuture<Boolean> add(BigInteger key, T t);

    ListenableFuture<Boolean> put(BigInteger key, T t);

    Future<Void> shutdown();
}
