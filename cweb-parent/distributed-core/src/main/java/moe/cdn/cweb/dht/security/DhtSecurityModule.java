package moe.cdn.cweb.dht.security;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

/**
 * @author davix
 */
public class DhtSecurityModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserKeyService.class).to(UserKeyServiceImpl.class).in(Singleton.class);
        bind(SignatureValidationService.class).to(SignatureValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebSignatureValidationService.class).to(CwebValidationServiceImpl.class)
                .in(Singleton.class);
    }
}
