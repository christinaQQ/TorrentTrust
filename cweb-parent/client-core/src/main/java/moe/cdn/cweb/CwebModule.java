package moe.cdn.cweb;

import java.util.Arrays;
import java.util.Random;

import com.google.inject.AbstractModule;

import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.security.CwebId;
import moe.cdn.cweb.security.KeyEnviroment;
import moe.cdn.cweb.security.SecurityModule;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.TrustNetworkModule;
import moe.cdn.cweb.vote.VoteModule;

public class CwebModule extends AbstractModule {

    private final GlobalEnvironment environment;

    public CwebModule(String... args) {
        this(Arrays.asList(args));
    }

    public CwebModule(Iterable<String> args) {
        environment = GlobalEnvironment.newBuilderFromArgs(args).setPort(1717)
                .setId(new CwebId(new Random())).setKeyPair(KeyUtils.generateKeyPair()).build();
    }

    @Override
    protected void configure() {
        bind(PeerEnvironment.class).toInstance(environment);
        bind(KeyEnviroment.class).toInstance(environment);

        install(new SecurityModule());
        install(new VoteModule());
        install(new TrustNetworkModule());

        bind(CwebApi.class).to(CwebApiImpl.class);
        bind(TrustGenerator.class).to(TrustGeneratorImpl.class);
    }

}
