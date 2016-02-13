package moe.cdn.cweb.dht;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class CwebMapFutureGetImpl<T> implements CwebFutureGet<T> {

    private final CwebFutureGet<T> parent;
    private final Predicate<T> filter;

    CwebMapFutureGetImpl(CwebFutureGet<T> parent, Predicate<T> filter) {
        this.parent = parent;
        this.filter = filter;
    }

    @Override
    public Collection<T> getAll() throws InterruptedException {
        return parent.getAll().stream().filter(filter).collect(Collectors.toList());
    }

    @Override
    public Collection<T> getAll(long timeout, TimeUnit units)
            throws InterruptedException, TimeoutException {
        return parent.getAll(timeout, units).stream().filter(filter).collect(Collectors.toList());
    }

}
