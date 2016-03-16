package moe.cdn.cweb;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import moe.cdn.cweb.dht.DhtModule;
import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.security.SecurityModule;
import moe.cdn.cweb.trust.TrustNetworkModule;
import moe.cdn.cweb.vote.VoteModule;

import java.util.Arrays;

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
        bind(PeerEnvironment.class).toInstance(new GlobalEnvironment(args));

        install(new DhtModule());
        install(new SecurityModule());
        install(new VoteModule());
        install(new TrustNetworkModule());

        bind(CwebApi.class).to(CwebApiImpl.class);
        bind(TrustGenerator.class).to(TrustGeneratorImpl.class);
    }

}
