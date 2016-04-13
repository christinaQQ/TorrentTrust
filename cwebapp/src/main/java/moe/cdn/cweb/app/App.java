package moe.cdn.cweb.app;

import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.net.URI;
import java.net.URL;

/**
 * @author davix
 */
public class App {
    private static final int DEFAULT_APP_PORT = 8080;

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option appPortOption = Option.builder()
                .longOpt("app-port")
                .hasArg()
                .type(Number.class)
                .argName("n")
                .desc("The port that will be used to communicate the status of the application.")
                .build();
        options.addOption(appPortOption);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        // List<String> argList = cmd.getArgList();
        int appPort;
        Object parsedOptionValue = cmd.getParsedOptionValue("app-port");
        if (parsedOptionValue == null) {
            appPort = DEFAULT_APP_PORT;
        } else {
            appPort = (int) parsedOptionValue;
        }

        Server server = new Server(appPort);

        ServletContextHandler servletHandler = new ServletContextHandler();

        DefaultServlet appServlet = new DefaultServlet();
        ServletHolder appHolder = new ServletHolder(appServlet);
        URL appFile = App.class.getClassLoader().getResource("app/build/js/main.js");
        if (appFile == null) {
            throw new RuntimeException("Cannot find application resources");
        }
        URI appDir = appFile.toURI().resolve("../").normalize();
        appHolder.setInitParameter("resourceBase", appDir.toURL().toExternalForm());
        appHolder.setInitParameter("pathInfoOnly", "true");
        appHolder.setInitParameter("dirAllowed", "false");
        servletHandler.addServlet(appHolder, "/app/build/*");

        DefaultServlet staticServlet = new DefaultServlet();
        ServletHolder staticHolder = new ServletHolder(staticServlet);

        URL staticFile = App.class.getClassLoader().getResource("static/giphy.gif");
        if (staticFile == null) {
            throw new RuntimeException("Cannot find static resources");
        }
        URI staticDir = staticFile.toURI().resolve("./").normalize();
        staticHolder.setInitParameter("resourceBase", staticDir.toURL().toExternalForm());
        staticHolder.setInitParameter("pathInfoOnly", "true");
        appHolder.setInitParameter("dirAllowed", "false");
        servletHandler.addServlet(staticHolder, "/static/*");

        servletHandler.addServlet(IndexServlet.class, "/");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { servletHandler });
        server.setHandler(handlers);
        server.start();

        server.join();
    }
}
