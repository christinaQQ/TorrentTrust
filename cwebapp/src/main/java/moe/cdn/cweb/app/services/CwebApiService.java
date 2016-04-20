package moe.cdn.cweb.app.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import moe.cdn.cweb.*;
import moe.cdn.cweb.app.App;
import moe.cdn.cweb.app.AppModule;
import moe.cdn.cweb.dht.DhtModuleService;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.annotations.DhtNodeController;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.CwebIdentityApi;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

/**
 * @author davix
 */
public class CwebApiService implements ServletContextListener {
    public static final String STATE_FILE_PATH_ATTRIBUTE =
            "moe.cdn.cweb.app.services.state-file-path";
    private static final int DEFAULT_DHT_PORT_1 = 1717;
    private static final int DEFAULT_DHT_PORT_2 = 1718;

    private int dhtPort1 = DEFAULT_DHT_PORT_1;
    private int dhtPort2 = DEFAULT_DHT_PORT_2;
    private String[] args;
    private ManagedPeer peerDht;

    public CwebApiService() {
        this.args = new String[0];
    }

    public CwebApiService(int dhtPort1, String... args) {
        this.dhtPort1 = dhtPort1;
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
            dhtPort1 = i;
        }

        String dataFileUriString =
                sce.getServletContext().getInitParameter(App.STATE_FILE_URI_INIT_PARAM);
        Path stateFilePath;
        try {
            stateFilePath = Paths.get(new URI(dataFileUriString));
        } catch (URISyntaxException e) {
            throw new CwebConfigurationException("Invalid URI for data file", e);
        }
        if (!Files.exists(stateFilePath)) {
            // ok we need to create a user identity here.. no idea how to do
            // that...
            SecurityProtos.KeyPair keyPair = KeyUtils.generateKeyPair();
            String publicKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublicKey().getRaw().toByteArray());
            String privateKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublicKey().getRaw().toByteArray());
            String[] lines = {"{",
                    "\"error_message\": null,",
                    "\"info_message\": null,",
                    "\"trusted_identities\": {\"" + publicKey + "\": []},",
                    "\"possible_trust_algorithms\": [",
                    "    {\"id\":\"EIGENTRUST\",\"name\":\"Eigentrust\"},",
                    "    {\"id\":\"ONLY_FRIENDS\",\"name\":\"Only friends\"},",
                    "    {\"id\":\"CONNECTED_COMPONENT\",\"name\":\"Anyone in your network\"},",
                    "    {\"id\":\"FRIENDS_OF_FRIENDS\",\"name\":\"Friends of friends\"}", "  ],",
                    "\"current_trust_algorithm\": {\"id\":\"ONLY_FRIENDS\",\"name\":\"Only "
                            + "Friends\"},",
                    "\"current_identity\": {\"name\":\"Default ID\",\"publicKey\":\"" + publicKey
                            + "\",\"privateKey\":\"" + privateKey + "\"},",
                    "\"user_identities\": [{\"name\":\"Default ID\",\"publicKey\":\"" + publicKey
                            + "\",\"privateKey\":\"" + privateKey + "\"}],",
                    "\"torrent_lists\": {\"" + publicKey + "\": []}", "}"};
            try {
                Files.write(stateFilePath, Arrays.asList(lines));
            } catch (IOException e) {
                throw new CwebConfigurationException("Cannot persist local state", e);
            }
        }
        sce.getServletContext().setAttribute(STATE_FILE_PATH_ATTRIBUTE, stateFilePath);

        // Initialize Guice modules
        AppModule appModule = new AppModule(dhtPort1, dhtPort2, args);
        Injector injector = Guice.createInjector(DhtModuleService.getInstance().getDhtModule(),
                CwebModuleService.getInstance().getCwebModule(), appModule);

        peerDht = injector.getInstance(Key.get(ManagedPeer.class, DhtNodeController.class));

        sce.getServletContext().setAttribute(GlobalEnvironment.class.getName(),
                appModule.getEnvironment());

        CwebApi cwebApi = injector.getInstance(CwebApi.class);
        sce.getServletContext().setAttribute(CwebApi.class.getName(), cwebApi);

        CwebTrustNetworkApi trustNetwork = injector.getInstance(CwebTrustNetworkApi.class);
        sce.getServletContext().setAttribute(CwebTrustNetworkApi.class.getName(), trustNetwork);
        
        CwebIdentityApi identityApi = injector.getInstance(CwebIdentityApi.class);
        sce.getServletContext().setAttribute(CwebIdentityApi.class.getName(), identityApi);

        CwebVoteApi voteService = injector.getInstance(CwebVoteApi.class);
        sce.getServletContext().setAttribute(CwebVoteApi.class.getName(), voteService);

        sce.getServletContext().setAttribute(IdentityEnvironment.class.getName(), appModule
                .getIdentities());

        TrustGenerator trustGenerator = injector.getInstance(TrustGenerator.class);
        sce.getServletContext().setAttribute(TrustGenerator.class.getName(), trustGenerator);

        TrustApi trustApi = injector.getInstance(TrustApi.class);
        sce.getServletContext().setAttribute(TrustApi.class.getName(), trustApi);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (peerDht != null) {
            try {
                peerDht.shutdown().get(); // TODO: Should this shutdown actually
                // be blocking?
            } catch (InterruptedException | ExecutionException e) {
                sce.getServletContext()
                        .log("An exception occurred while shutting down the local DHT node.", e);
            }
        }
    }
}
