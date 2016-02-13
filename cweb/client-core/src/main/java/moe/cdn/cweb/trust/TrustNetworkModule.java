package moe.cdn.cweb.trust;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class TrustNetworkModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebTrustNetworkService.class).to(CwebTrustNetworkServiceImpl.class).in(Singleton.class);
    }

}
