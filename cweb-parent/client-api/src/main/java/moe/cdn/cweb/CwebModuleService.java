package moe.cdn.cweb;

import moe.cdn.cweb.spi.CwebModule;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Service that provides a {@link CwebModule}.
 *
 * @author davix
 */
public class CwebModuleService {
    private final ServiceLoader<CwebModule> loader;
    private CwebModule module;

    private CwebModuleService() {
        loader = ServiceLoader.load(CwebModule.class);
    }

    public static CwebModuleService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public CwebModule getCwebModule() {
        if (module == null) {
            Iterator<CwebModule> it = loader.iterator();
            if (!it.hasNext()) {
                throw new NoSuchServiceProviderException();
            }
            module = it.next();
        }
        return module;
    }

    private static class LazyHolder {
        private static final CwebModuleService INSTANCE = new CwebModuleService();
    }
}
