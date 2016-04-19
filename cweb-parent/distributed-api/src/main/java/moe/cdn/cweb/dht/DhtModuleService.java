package moe.cdn.cweb.dht;

import moe.cdn.cweb.NoSuchServiceProviderException;
import moe.cdn.cweb.dht.spi.DhtModule;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Service that provides a {@link DhtModule}.
 *
 * @author davix
 */
public class DhtModuleService {
    private final ServiceLoader<DhtModule> loader;
    private DhtModule module;

    private DhtModuleService() {
        loader = ServiceLoader.load(DhtModule.class);
    }

    public static DhtModuleService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public DhtModule getDhtModule() {
        if (module == null) {
            Iterator<DhtModule> it = loader.iterator();
            if (!it.hasNext()) {
                throw new NoSuchServiceProviderException();
            }
            module = it.next();
        }
        return module;
    }

    private static class LazyHolder {
        private static final DhtModuleService INSTANCE = new DhtModuleService();
    }
}
