package moe.cdn.cweb.security;

import com.google.inject.AbstractModule;

import javax.inject.Singleton;

public class SecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
