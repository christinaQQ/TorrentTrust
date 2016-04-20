package moe.cdn.cweb.app;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

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

/**
 * @author davix
 */
public class AppModule extends AbstractModule {
    private final GlobalEnvironment environment;
    private final IdentityEnvironment identities;

    public AppModule(int port1, int port2, String... args) {
        IdentityEnvironment identityEnvironment;
        URI identityUri;
        try {
            identityUri = getClass().getClassLoader().getResource("identities.ini").toURI();
            identityEnvironment = IdentityEnvironment.readFromFile(new File(identityUri));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            identityUri = URI.create("~/.cweb/identities.ini");
            identityEnvironment = new IdentityEnvironment(KeyUtils.generateKeyPair(), "Default");
        }
        identities = identityEnvironment;
        environment = GlobalEnvironment.newBuilderFromArgs(args)
                .setPort1(port1)
                .setPort2(port2)
                .setId(new CwebId(new Random())).setKeyEnvironment(identities)
                .setKeyEnvironmentConfigPath(identityUri).build();
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
