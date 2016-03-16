package moe.cdn.cweb;

import moe.cdn.cweb.security.utils.CwebId;
import java.util.Arrays;
import java.util.Random;

import com.google.inject.AbstractModule;

import moe.cdn.cweb.dht.DhtModule;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.security.SecurityModule;
import moe.cdn.cweb.trust.TrustNetworkModule;
import moe.cdn.cweb.vote.VoteModule;

public class CwebModule extends AbstractModule {

    private final Iterable<String> args;

    public CwebModule(String... args) {
        this.args = Arrays.asList(args);
    }

    public CwebModule(Iterable<String> args) {
        this.args = args;
    }

    @Override
    protected void configure() {
        // TODO: allow configuring ports
        // TODO: allow setting the local node id
        bind(PeerEnvironment.class).toInstance(GlobalEnvironment.newBuilderFromArgs(args)
                .setPort(1717).setId(new CwebId(new Random())).build());

        install(new DhtModule());
        install(new SecurityModule());
        install(new VoteModule());
        install(new TrustNetworkModule());

        bind(CwebApi.class).to(CwebApiImpl.class);
        bind(TrustGenerator.class).to(TrustGeneratorImpl.class);
    }

}
