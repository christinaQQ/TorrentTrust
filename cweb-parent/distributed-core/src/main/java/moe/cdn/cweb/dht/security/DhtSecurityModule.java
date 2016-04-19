package moe.cdn.cweb.dht.security;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

/**
 * @author davix
 */
public class DhtSecurityModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(KeyLookupService.class).to(KeyLookupServiceImpl.class).in(Singleton.class);
        bind(SignatureValidationService.class).to(SignatureValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebSignatureValidationService.class).to(CwebValidationServiceImpl.class)
                .in(Singleton.class);
    }
}
