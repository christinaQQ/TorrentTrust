package moe.cdn.cweb.security;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class SecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(KeyLookupService.class).to(KeyLookupServiceImpl.class).in(Singleton.class);
        bind(SignatureValidationService.class).to(SignatureValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebSignatureValidationService.class).to(CwebValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);;
    }

}
