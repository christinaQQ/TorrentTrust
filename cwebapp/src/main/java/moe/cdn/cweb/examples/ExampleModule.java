package moe.cdn.cweb.examples;

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

import java.net.URI;
import java.util.Random;

/**
 * @author davix
 */
public class ExampleModule extends AbstractModule {
    private final GlobalEnvironment environment;

    public ExampleModule(int port, String... args) {
        environment = GlobalEnvironment.newBuilderFromArgs(args).setPort(port)
                .setId(new CwebId(new Random()))
                .setKeyEnvironment(new IdentityEnvironment(KeyUtils.generateKeyPair(), "Default"))
                .setKeyEnvironmentConfigPath(URI.create("identities.ini"))
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

}
