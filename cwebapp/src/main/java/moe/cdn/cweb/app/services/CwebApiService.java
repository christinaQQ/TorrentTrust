package moe.cdn.cweb.app.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.CwebModuleService;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.IdentityEnvironment;
import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.TrustApi;
import moe.cdn.cweb.TrustGenerator;
import moe.cdn.cweb.app.App;
import moe.cdn.cweb.app.AppModule;
import moe.cdn.cweb.dht.DhtModuleService;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.annotations.KeyLookupDhtNodeController;
import moe.cdn.cweb.dht.annotations.PrimaryDhtNodeController;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.CwebIdentityApi;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

/**
 * @author davix
 */
public class CwebApiService implements ServletContextListener {
    public static volatile Injector injector;
    public static final String STATE_FILE_PATH_ATTRIBUTE =
            "moe.cdn.cweb.app.services.state-file-path";
    private static final int DEFAULT_DHT_PORT_1 = 1717;
    private static final int DEFAULT_DHT_PORT_2 = 1718;
    private static final Path DEFAULT_STATE_FILE_PATH = Paths.get("state.json");

    private int dhtPort1 = DEFAULT_DHT_PORT_1;
    private int dhtPort2 = DEFAULT_DHT_PORT_2;
    private String[] args;
    private ManagedPeer primaryNode;
    private ManagedPeer keyLookupNode;

    public CwebApiService() {
        this.args = new String[0];
    }

    public CwebApiService(int dhtPort1, int dhtPort2, String... args) {
        this.dhtPort1 = dhtPort1;
        this.dhtPort2 = dhtPort2;
        this.args = args;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dhtPort1String = sce.getServletContext().getInitParameter(App.DHT_PORT_1_INIT_PARAM);
        if (dhtPort1String != null) {
            int i;
            try {
                i = Integer.parseInt(dhtPort1String);
            } catch (NumberFormatException e) {
                throw new CwebConfigurationException("DHT port must be an integer", e);
            }
            dhtPort1 = i;
        }
        String dhtPort2String = sce.getServletContext().getInitParameter(App.DHT_PORT_2_INIT_PARAM);
        if (dhtPort2String != null) {
            int i;
            try {
                i = Integer.parseInt(dhtPort2String);
            } catch (NumberFormatException e) {
                throw new CwebConfigurationException("DHT port must be an integer", e);
            }
            dhtPort2 = i;
        }

        String dataFileUriString =
                sce.getServletContext().getInitParameter(App.STATE_FILE_URI_INIT_PARAM);
        Path stateFilePath;
        if (dataFileUriString == null) {
            stateFilePath = DEFAULT_STATE_FILE_PATH;
        } else {
            try {
                stateFilePath = Paths.get(new URI(dataFileUriString));
            } catch (URISyntaxException e) {
                throw new CwebConfigurationException("Invalid URI for data file", e);
            }
        }
        if (!Files.exists(stateFilePath)) {
            // ok we need to create a user identity here.. no idea how to do
            // that...
            String[] lines = {"{", "\"error_message\": null,", "\"info_message\": null,",
                    "\"trusted_identities\": {},", "\"possible_trust_algorithms\": [",
                    "    {\"id\":\"EIGENTRUST\",\"name\":\"Eigentrust\"},",
                    "    {\"id\":\"ONLY_FRIENDS\",\"name\":\"Only friends\"},",
                    "    {\"id\":\"CONNECTED_COMPONENT\",\"name\":\"Anyone in your network\"},",
                    "    {\"id\":\"FRIENDS_OF_FRIENDS\",\"name\":\"Friends of friends\"}", "  ],",
                    "\"current_trust_algorithm\": {\"id\":\"ONLY_FRIENDS\",\"name\":\"Only "
                            + "Friends\"},",
                    "\"current_identity\": null,", "\"user_identities\": [],",
                    "\"torrent_lists\": {}", "}"};
            try {
                Files.write(stateFilePath, Arrays.asList(lines));
            } catch (IOException e) {
                throw new CwebConfigurationException("Cannot persist local state", e);
            }
        }
        sce.getServletContext().setAttribute(STATE_FILE_PATH_ATTRIBUTE, stateFilePath);

        // Initialize Guice modules
        AppModule appModule = null;
        try {
            appModule = new AppModule(dhtPort1, dhtPort2, args);
        } catch (IOException e) {
            throw new CwebConfigurationException(e);
        }
        injector = Guice.createInjector(DhtModuleService.getInstance().getDhtModule(),
                CwebModuleService.getInstance().getCwebModule(), appModule);

        injectAndProvideApi(sce, CwebApi.class, injector);
        injectAndProvideApi(sce, CwebTrustNetworkApi.class, injector);
        injectAndProvideApi(sce, CwebIdentityApi.class, injector);
        injectAndProvideApi(sce, CwebVoteApi.class, injector);
        injectAndProvideApi(sce, TrustApi.class, injector);
        injectAndProvideApi(sce, TrustGenerator.class, injector);

        primaryNode =
                injector.getInstance(Key.get(ManagedPeer.class, PrimaryDhtNodeController.class));
        keyLookupNode =
                injector.getInstance(Key.get(ManagedPeer.class, KeyLookupDhtNodeController.class));

        provideApi(sce, GlobalEnvironment.class, appModule.getEnvironment());
        provideApi(sce, IdentityEnvironment.class, appModule.getIdentities());
        provideApi(sce, ManagedPeer.class, primaryNode);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (primaryNode != null) {
            try {
                primaryNode.shutdown().get(); // TODO: Should this shutdown
                                              // actually
                // be blocking?
            } catch (InterruptedException | ExecutionException e) {
                sce.getServletContext().log(
                        "An exception occurred while shutting down the local primary DHT node.", e);
            }
        }
        if (primaryNode != null) {
            try {
                keyLookupNode.shutdown().get();
            } catch (InterruptedException | ExecutionException e) {
                sce.getServletContext().log(
                        "An exception occurred while shutting down the local key lookup DHT node.",
                        e);
            }
        }
    }

    private static <E> void provideApi(ServletContextEvent sce, Class<E> clazz, E instance) {
        sce.getServletContext().setAttribute(clazz.getName(), instance);
    }

    private static <E> void injectAndProvideApi(ServletContextEvent sce,
            Class<E> clazz,
            Injector injector) {
        provideApi(sce, clazz, injector.getInstance(clazz));
    }

}
