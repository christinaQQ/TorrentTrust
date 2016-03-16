package moe.cdn.cweb;

import com.google.inject.AbstractModule;
import moe.cdn.cweb.dht.DhtModule;
import moe.cdn.cweb.security.SecurityModule;
import moe.cdn.cweb.trust.TrustNetworkModule;
import moe.cdn.cweb.vote.VoteModule;

public class CwebModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new DhtModule());
        install(new SecurityModule());
        install(new VoteModule());
        install(new TrustNetworkModule());

        bind(CwebApi.class).to(CwebApiImpl.class);
        bind(TrustGenerator.class).to(TrustGeneratorImpl.class);
    }

}
