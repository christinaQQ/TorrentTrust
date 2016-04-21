package moe.cdn.cweb.app;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.glassfish.jersey.servlet.ServletContainer;

import com.google.inject.servlet.ServletModule;

import moe.cdn.cweb.app.api.CwebApiConfig;
import moe.cdn.cweb.app.services.CwebConfigurationException;

/**
 * @author davix
 */
public class AppServletModule extends ServletModule {

    private static Map<String, String> newFileServletInitParameters(URL resourceDirUrl) {
        Map<String, String> appInitParameters = new HashMap<>();
        String resourceBase = resourceDirUrl.toExternalForm();
        appInitParameters.put("resourceBase", resourceBase);
        appInitParameters.put("pathInfoOnly", "true");
        appInitParameters.put("dirAllowed", "false");
        return appInitParameters;
    }

    @Override
    protected void configureServlets() {
        bind(HydrationServlet.class).in(Singleton.class);
        bind(DefaultServlet.class).in(Singleton.class);
        bind(ServletContainer.class).in(Singleton.class);

        serve("/api/*").with(new ServletContainer(new CwebApiConfig()));

        URL appDirUrl = getClass().getResource("/app/build/");
        if (appDirUrl == null) {
            throw new CwebConfigurationException("Cannot find application resources");
        }
        serve("/app/build/*").with(DefaultServlet.class, newFileServletInitParameters(appDirUrl));

        URL staticDirUrl = getClass().getResource("/static/");
        if (staticDirUrl == null) {
            throw new CwebConfigurationException("Cannot find static resources");
        }
        serve("/static/*").with(DefaultServlet.class, newFileServletInitParameters(staticDirUrl));
        serve("/hydration.js").with(HydrationServlet.class);
        serve("/*").with(IndexServlet.class);
    }
}
