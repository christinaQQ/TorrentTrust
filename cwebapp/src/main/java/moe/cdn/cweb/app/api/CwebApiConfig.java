package moe.cdn.cweb.app.api;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import moe.cdn.cweb.app.api.exceptions.mappers.ConflictExceptionMapper;
import moe.cdn.cweb.app.api.exceptions.mappers.NoSuchThingExceptionMapper;

@ApplicationPath("api")
public class CwebApiConfig extends ResourceConfig {

    public CwebApiConfig() {
        register(LoggingFilter.class);

        register(UriConnegFilter.class);

        register(ConflictExceptionMapper.class);
        register(NoSuchThingExceptionMapper.class);

        packages("moe.cdn.cweb.app.api.resources");
    }

}
