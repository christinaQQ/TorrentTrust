package moe.cdn.cweb.app.api;

import moe.cdn.cweb.app.util.Base64StringBytesAdapter;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("api")
public class CwebApiConfig extends ResourceConfig {

    public CwebApiConfig() {
        register(LoggingFilter.class);

        register(UriConnegFilter.class);

        register(Base64StringBytesAdapter.class);

        packages("moe.cdn.cweb.app.api");
    }

}