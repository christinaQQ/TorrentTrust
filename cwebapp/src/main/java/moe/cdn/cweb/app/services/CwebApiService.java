package moe.cdn.cweb.app.services;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import moe.cdn.cweb.CwebApi;
import moe.cdn.cweb.CwebModuleService;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.IdentityEnvironment;
import moe.cdn.cweb.app.App;
import moe.cdn.cweb.app.AppModule;
import moe.cdn.cweb.dht.DhtModuleService;
import moe.cdn.cweb.dht.ManagedPeer;
import moe.cdn.cweb.dht.annotations.DhtNodeController;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;
import moe.cdn.cweb.vote.CwebVoteApi;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

/**
 * @author davix
 */
public class CwebApiService implements ServletContextListener {
    public static final String STATE_FILE_PATH_ATTRIBUTE =
            "moe.cdn.cweb.app.services.state-file-path";
    private static final int DEFAULT_DHT_PORT = 1717;

    private int dhtPort = DEFAULT_DHT_PORT;
    private String[] args;
    private ManagedPeer peerDht;

    public CwebApiService() {
        this.args = new String[0];
    }

    public CwebApiService(int dhtPort, String... args) {
        this.dhtPort = dhtPort;
        this.args = args;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dhtPortString = sce.getServletContext().getInitParameter(App.DHT_PORT_INIT_PARAM);
        if (dhtPortString != null) {
            int i;
            try {
                i = Integer.parseInt(dhtPortString);
            } catch (NumberFormatException e) {
                throw new CwebConfigurationException("DHT port must be an integer", e);
            }
            dhtPort = i;
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
            String pubKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublicKey().getRaw().toByteArray());
            String privKey = Base64.getEncoder()
                    .encodeToString(keyPair.getPublicKey().getRaw().toByteArray());
            String[] lines = {"{",
                    "\"error_message\": null,",
                    "\"info_message\": null,",
                    "\"trusted_identities\": {\"" + pubKey + "\": []},",
                    "\"possible_trust_algorithms\": [",
                    "    {\"id\": \"EIGENTRUST\", \"name\": \"Eigentrust\"},",
                    "    {\"id\": \"ONLY_FRIENDS\", \"name\": \"Only Friends\"},",
                    "    {\"id\": \"FRIEND_OF_FRIEND\", \"name\": \"Friends of friends\"}", "  ],",
                    "\"current_trust_algorithm\": {\"id\": \"ONLY_FRIENDS\", \"name\": \"Only "
                            + "Friends\"},",
                    "\"current_identity\": {\"name\": \"Default ID\", \"pubKey\": \" " + pubKey
                            + " \", \"privateKey\": \"" + privKey + "\"}",
                    "\"user_identities\": [{\"name\": \"Default ID\", \"pubKey\": \" " + pubKey
                            + " \", \"privateKey\": \"" + privKey + "\"}]",
                    "\"torrent_lists\": {\"" + pubKey + "\": []}", "}"};
            try {
                Files.write(stateFilePath, Arrays.asList(lines));
            } catch (IOException e) {
                throw new CwebConfigurationException("Cannot persist local state", e);
            }
        }
        sce.getServletContext().setAttribute(STATE_FILE_PATH_ATTRIBUTE, stateFilePath);

        // Initialize Guice modules
        AppModule appModule = new AppModule(dhtPort, args);
        Injector injector = Guice.createInjector(DhtModuleService.getInstance().getDhtModule(),
                CwebModuleService.getInstance().getCwebModule(), appModule);

        peerDht = injector.getInstance(Key.get(ManagedPeer.class, DhtNodeController.class));

        CwebApi cwebApi = injector.getInstance(CwebApi.class);
        sce.getServletContext().setAttribute(CwebApi.class.getName(), cwebApi);

        CwebTrustNetworkApi trustNetwork = injector.getInstance(CwebTrustNetworkApi.class);
        sce.getServletContext().setAttribute(CwebTrustNetworkApi.class.getName(), trustNetwork);

        CwebVoteApi voteService = injector.getInstance(CwebVoteApi.class);
        sce.getServletContext().setAttribute(CwebVoteApi.class.getName(), voteService);

        sce.getServletContext().setAttribute(IdentityEnvironment.class.getName(), appModule.getIdentities());
        
        sce.getServletContext().setAttribute(GlobalEnvironment.class.getName(),
                appModule.getEnvironment());
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
