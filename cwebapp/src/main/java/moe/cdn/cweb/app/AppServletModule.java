package moe.cdn.cweb.app;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
//    private static final boolean DEVELOPMENT = true;

    private static Map<String, String> newFileServletInitParameters(URL resourceDirUrl) {
        Map<String, String> appInitParameters = new HashMap<>();
        String resourceBase = resourceDirUrl.toExternalForm();
//        if (DEVELOPMENT) {
//            if (resourceBase.endsWith("build/")) {
//                resourceBase = "src/main/resources/app/build/";
//            } else if (resourceBase.endsWith("static/")) {
//                resourceBase = "src/main/resources/app/static/";
//            } else {
//                throw new CwebConfigurationException(
//                        "Unknown resourceBase init parameter: " + resourceBase);
//            }
//        }
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

        URL appFile = App.class.getClassLoader().getResource("app/build/js/main.js");
        if (appFile == null) {
            throw new CwebConfigurationException("Cannot find application resources");
        }
        URL appDirUrl;
        try {
            URI appDir = appFile.toURI().resolve("../").normalize();
            appDirUrl = appDir.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new CwebConfigurationException("Cannot resolve application resources", e);
        }
        serve("/app/build/*").with(new DefaultServlet(), newFileServletInitParameters(appDirUrl));

        URL staticFile = App.class.getClassLoader().getResource("static/giphy.gif");
        if (staticFile == null) {
            throw new CwebConfigurationException("Cannot find static resources");
        }
        URL staticDirUrl;
        try {
            URI staticDir = staticFile.toURI().resolve("./").normalize();
            staticDirUrl = staticDir.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new CwebConfigurationException("Cannot resolve application resources", e);
        }
        serve("/static/*").with(new DefaultServlet(), newFileServletInitParameters(staticDirUrl));
        serve("/hydration.js").with(HydrationServlet.class);
        serve("/*").with(IndexServlet.class);
    }
}
