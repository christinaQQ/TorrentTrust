package moe.cdn.cweb.security;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class SecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
