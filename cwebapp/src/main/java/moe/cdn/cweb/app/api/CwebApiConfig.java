package moe.cdn.cweb.app.api;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import moe.cdn.cweb.app.util.Base64StringBytesAdapter;

@ApplicationPath("api")
public class CwebApiConfig extends ResourceConfig {

    public CwebApiConfig() {
        register(LoggingFilter.class);

        register(UriConnegFilter.class);

        register(NoSuchThingExceptionHandler.class);

        packages("moe.cdn.cweb.app.api");
    }

}
