package moe.cdn.cweb.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.inject.servlet.GuiceFilter;

import moe.cdn.cweb.app.services.CwebApiService;

/**
 * @author davix
 */
public class App {
    public static final String DHT_PORT_1_INIT_PARAM = "moe.cdn.cweb.app.dht-port-1";
    public static final String DHT_PORT_2_INIT_PARAM = "moe.cdn.cweb.app.dht-port-2";
    public static final String STATE_FILE_URI_INIT_PARAM = "moe.cdn.cweb.app.data-file-path";
    private static final int DEFAULT_APP_PORT = 8080;
    private static final int DEFAULT_DHT_PORT_1 = 1717;
    private static final int DEFAULT_DHT_PORT_2 = 1718;
    private static final String DEFAULT_DATA_DIR_PATH = ".";
    private static final String DATA_FILENAME = "state.json";

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        Option appPortOption = Option.builder().longOpt("app-port").hasArg().type(Number.class)
                .argName("n").desc("The port that will be used to communicate the status of the "
                        + "application.")
                .build();
        Option dhtPort1Option = Option.builder().longOpt("dht-port-1").hasArg().type(Number.class)
                .argName("d1")
                .desc("The port that will be used for the primary dht node to listen on").build();
        Option dhtPort2Option = Option.builder().longOpt("dht-port-2").hasArg().type(Number.class)
                .argName("d2")
                .desc("The port that will be used for the secondary dht node to listen on").build();
        Option flatFileOption =
                Option.builder().longOpt("data-dir").hasArg().type(String.class).argName("s")
                        .desc("The file that stores the data for this users identities, votes, etc.")
                        .build();
        options.addOption(appPortOption);
        options.addOption(flatFileOption);
        options.addOption(dhtPort1Option);
        options.addOption(dhtPort2Option);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        int appPort;
        Object parsedOptionValue = cmd.getParsedOptionValue("app-port");
        if (parsedOptionValue == null) {
            appPort = DEFAULT_APP_PORT;
        } else {
            appPort = ((Number) parsedOptionValue).intValue();
        }
        Object parsedFileOptionValue = cmd.getParsedOptionValue("data-dir");
        Path statePath;
        if (parsedFileOptionValue == null) {
            statePath = Paths.get(DEFAULT_DATA_DIR_PATH, DATA_FILENAME);
        } else {
            statePath = Paths.get((String) parsedFileOptionValue, DATA_FILENAME);
        }
        int dhtPort1;
        Object parsedDhtPort1Value = cmd.getParsedOptionValue("dht-port-1");
        if (parsedDhtPort1Value == null) {
            dhtPort1 = DEFAULT_DHT_PORT_1;
        } else {
            dhtPort1 = ((Number) parsedDhtPort1Value).intValue();
        }
        int dhtPort2;
        Object parsedDhtPort2Value = cmd.getParsedOptionValue("dht-port-2");
        if (parsedDhtPort2Value == null) {
            dhtPort2 = DEFAULT_DHT_PORT_2;
        } else {
            dhtPort2 = ((Number) parsedDhtPort2Value).intValue();
        }

        Server server = new Server(appPort);

        ServletContextHandler servletHandler = new ServletContextHandler();

        servletHandler.setInitParameter(STATE_FILE_URI_INIT_PARAM, statePath.toUri().toString());
        servletHandler.setInitParameter(DHT_PORT_1_INIT_PARAM, String.valueOf(dhtPort1));
        servletHandler.setInitParameter(DHT_PORT_2_INIT_PARAM, String.valueOf(dhtPort2));

        servletHandler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        servletHandler.addServlet(new ServletHolder(new DefaultServlet()), "/");
        servletHandler.addEventListener(new CwebGuiceServletConfig());
        servletHandler.addEventListener(new CwebApiService());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {servletHandler});
        server.setHandler(handlers);
        server.start();

        server.join();
    }
}
