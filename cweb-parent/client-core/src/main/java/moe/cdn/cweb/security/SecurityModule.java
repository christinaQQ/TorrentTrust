package moe.cdn.cweb.security;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.dht.KeyEnvironment;

public class SecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
