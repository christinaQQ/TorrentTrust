package moe.cdn.cweb;

import moe.cdn.cweb.dht.PeerEnvironment;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.security.SecurityModule;
import moe.cdn.cweb.spi.CwebModule;
import moe.cdn.cweb.trust.TrustNetworkModule;
import moe.cdn.cweb.vote.VoteModule;

public class CwebModuleImpl extends CwebModule {

    @Override
    protected void configure() {
        requireBinding(PeerEnvironment.class);

        install(new SecurityModule());
        install(new VoteModule());
        install(new TrustNetworkModule());

        bind(CwebApi.class).to(CwebApiImpl.class);
        bind(TrustGenerator.class).to(TrustGeneratorImpl.class);
    }

}
