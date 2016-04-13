package moe.cdn.cweb.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import moe.cdn.cweb.app.services.CwebApiService;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * @author davix
 */
public class App {
    private static final int DEFAULT_APP_PORT = 8080;
    public static final String DHT_PORT_INIT_PARAM = "dht-port";

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

        Injector injector = Guice.createInjector(new AppServletModule());

        Server server = new Server(appPort);

        ServletContextHandler servletHandler = new ServletContextHandler();

        servletHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        servletHandler.addServlet(DefaultServlet.class, "/");
        servletHandler.addEventListener(new CwebApiService(1717));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { servletHandler });
        server.setHandler(handlers);
        server.start();

        server.join();
    }
}
