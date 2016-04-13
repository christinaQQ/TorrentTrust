package moe.cdn.cweb.app;

import com.google.inject.servlet.ServletModule;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author davix
 */
public class AppServletModule extends ServletModule {
    @Override
    protected void configureServlets() {
        bind(DefaultServlet.class).in(Singleton.class);

        URL appFile = App.class.getClassLoader().getResource("app/build/js/main.js");
        if (appFile == null) {
            throw new RuntimeException("Cannot find application resources");
        }
        URL appDirUrl;
        try {
            URI appDir = appFile.toURI().resolve("../").normalize();
            appDirUrl = appDir.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException("Cannot resolve application resources", e);
        }
        serve("/app/build/*").with(DefaultServlet.class, newInitParameters(appDirUrl));

        URL staticFile = App.class.getClassLoader().getResource("static/giphy.gif");
        if (staticFile == null) {
            throw new RuntimeException("Cannot find static resources");
        }
        URL staticDirUrl;
        try {
            URI staticDir = staticFile.toURI().resolve("./").normalize();
            staticDirUrl = staticDir.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException("Cannot resolve application resources", e);
        }
        serve("/static/*").with(DefaultServlet.class, newInitParameters(staticDirUrl));
        serve("/*").with(IndexServlet.class);
    }

    private static Map<String, String> newInitParameters(URL resourceDirUrl) {
        Map<String, String> appInitParameters = new HashMap<>();
        appInitParameters.put("resourceBase", resourceDirUrl.toExternalForm());
        appInitParameters.put("pathInfoOnly", "true");
        appInitParameters.put("dirAllowed", "false");
        return appInitParameters;
    }
}
