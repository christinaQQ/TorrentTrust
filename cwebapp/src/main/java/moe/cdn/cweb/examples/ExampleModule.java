package moe.cdn.cweb.examples;

import com.google.inject.AbstractModule;
import moe.cdn.cweb.GlobalEnvironment;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.KeyEnvironment;
import moe.cdn.cweb.security.utils.KeyUtils;

import java.util.Random;

/**
 * @author davix
 */
public class ExampleModule extends AbstractModule {
    private final GlobalEnvironment environment;

    public ExampleModule(String[] args) {
        environment = GlobalEnvironment.newBuilderFromArgs(args)
                .setPort(1717)
                .setId(new CwebId(new Random())).setKeyPair(KeyUtils.generateKeyPair()).build();;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(UserDomain.class).to("user");
        bindConstant().annotatedWith(VoteDomain.class).to("vote");

        bind(PeerEnvironment.class).toInstance(environment);
        bind(KeyEnvironment.class).toInstance(environment);
    }

}
