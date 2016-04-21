package moe.cdn.cweb.app;

import com.google.inject.AbstractModule;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.IdentityEnvironment;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.annotations.VoteHistoryDomain;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.utils.KeyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Random;

/**
 * @author davix
 */
public class AppModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger();
    private final GlobalEnvironment environment;
    private final IdentityEnvironment identities;

    public AppModule(int port1, int port2, String... args) throws IOException {
        // FIXME: identity file might not be in the classpath
        try (InputStream is = getClass().getResourceAsStream("identities.ini")) {
            if (is == null) {
                identities = new IdentityEnvironment(KeyUtils.generateKeyPair(), "anonymous");
                logger.warn("Cannot find identity configuration file. "
                        + "A new key pair was generated.");
            } else {
                identities = IdentityEnvironment.readFromStream(is);
            }
        }
        environment = GlobalEnvironment.newBuilderFromArgs(args)
                .setPort1(port1)
                .setPort2(port2)
                .setId(new CwebId(new Random())).setKeyEnvironment(identities)
                .setKeyEnvironmentConfigPath(
                        Paths.get(System.getProperty("user.home"), ".cweb", "config.ini").toUri())
                .build();
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(UserDomain.class).to("user");
        bindConstant().annotatedWith(VoteDomain.class).to("vote");
        bindConstant().annotatedWith(VoteHistoryDomain.class).to("vote_history");

        bind(PeerEnvironment.class).toInstance(environment);
        bind(KeyEnvironment.class).toInstance(environment);
    }

    public GlobalEnvironment getEnvironment() {
        return environment;
    }

    public IdentityEnvironment getIdentities() {
        return identities;
    }

}
