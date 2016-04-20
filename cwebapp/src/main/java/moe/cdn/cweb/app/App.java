package moe.cdn.cweb.app;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import moe.cdn.cweb.app.services.CwebApiService;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

/**
 * @author davix
 */
public class App {
    public static final String DHT_PORT_INIT_PARAM = "moe.cdn.cweb.app.dht-port";
    public static final String STATE_FILE_URI_INIT_PARAM = "moe.cdn.cweb.app.data-file-path";

    private static final int DEFAULT_APP_PORT = 8080;
    private static final String DEFAULT_DATA_DIR_PATH = ".";
    private static final String DATA_FILENAME = "state.json";


    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        Options options = new Options();
        Option appPortOption =
                Option.builder().longOpt("app-port").hasArg().type(Number.class).argName("n")
                        .desc("The port that will be used to communicate the status of the "
                                + "application.")
                        .build();
        Option flatFileOption =
                Option.builder().longOpt("data-dir").hasArg().type(String.class).argName("s")
                        .desc("The file that stores the data for this users identities, votes, "
                                + "etc.")
                        .build();
        options.addOption(appPortOption);
        options.addOption(flatFileOption);
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
        Object parsedFileOptionValue = cmd.getParsedOptionValue("data-file");
        Path statePath;
        if (parsedFileOptionValue == null) {
            statePath = Paths.get(DEFAULT_DATA_DIR_PATH, DATA_FILENAME);
        } else {
            statePath = Paths.get((String) parsedFileOptionValue, DATA_FILENAME);
        }

        Server server = new Server(appPort);

        ServletContextHandler servletHandler = new ServletContextHandler();

        servletHandler.setInitParameter(STATE_FILE_URI_INIT_PARAM, statePath.toUri().toString());
        servletHandler.setInitParameter(DHT_PORT_INIT_PARAM, String.valueOf(1717));

        servletHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        servletHandler.addServlet(DefaultServlet.class, "/");
        servletHandler.addEventListener(new CwebGuiceServletConfig());
        servletHandler.addEventListener(new CwebApiService());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {servletHandler});
        server.setHandler(handlers);
        server.start();

        server.join();
    }
}
