package moe.cdn.cweb.security;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.SecurityProtos.KeyPair;

public class SecurityModule extends AbstractModule {

    @Provides
    @Singleton
    static KeyPair provideKeyPair(KeyEnviroment enviroment) {
        return enviroment.getKeyPair();
    }

    @Override
    protected void configure() {
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
