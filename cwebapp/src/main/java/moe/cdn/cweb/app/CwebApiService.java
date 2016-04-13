package moe.cdn.cweb.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.CwebModuleService;
import moe.cdn.cweb.dht.DhtModuleService;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.annotations.DhtNodeController;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutionException;

/**
 * @author davix
 */
public class CwebApiService implements ServletContextListener {
    private static final int DEFAULT_DHT_PORT = 1717;

    private int dhtPort;
    private String[] args;
    private volatile CwebApi instance;
    private ManagedPeer peerDht;

    public CwebApiService(int dhtPort, String... args) {
        this.dhtPort = dhtPort;
        this.args = args;
    }

    private CwebApi prepareCwebApi() {
        Injector injector = Guice.createInjector(DhtModuleService.getInstance().getDhtModule(),
                CwebModuleService.getInstance().getCwebModule(), new AppModule(dhtPort, args));
        peerDht = injector.getInstance(Key.get(ManagedPeer.class, DhtNodeController.class));
        return injector.getInstance(CwebApi.class);
    }

    public CwebApi getInstance() {
        CwebApi result = instance;
        if (result == null) {
            synchronized (this) {
                result = instance;
                if (result == null) {
                    instance = result = prepareCwebApi();
                }
            }
        }
        return result;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String value = sce.getServletContext().getInitParameter(App.DHT_PORT_INIT_PARAM);
        if (value != null) {
            int i;
            try {
                i = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("DHT port must be an integer", e);
            }
            dhtPort = i;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (peerDht != null) {
            try {
                peerDht.shutdown().get(); // TODO: Should this shutdown actually be blocking?
            } catch (InterruptedException | ExecutionException e) {
                sce.getServletContext().log(
                        "An exception occurred while shutting down the local DHT node.", e);
            }
        }
    }
}
