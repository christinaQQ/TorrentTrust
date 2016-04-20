package moe.cdn.cweb.trust;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class TrustNetworkModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebTrustNetworkApi.class).to(CwebTrustNetworkApiImpl.class).in(Singleton.class);
        bind(CwebIdentityApi.class).to(CwebTrustNetworkApiImpl.class).in(Singleton.class);
    }

}
