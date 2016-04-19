package moe.cdn.cweb.app.api;

import javax.ws.rs.ApplicationPath;

import moe.cdn.cweb.app.api.exceptions.handlers.NoSuchThingExceptionMapper;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;

@ApplicationPath("api")
public class CwebApiConfig extends ResourceConfig {

    public CwebApiConfig() {
        register(LoggingFilter.class);

        register(UriConnegFilter.class);

        register(NoSuchThingExceptionMapper.class);

        packages("moe.cdn.cweb.app.api");
    }

}
