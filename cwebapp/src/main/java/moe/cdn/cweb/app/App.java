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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        Option flatFileOption = Option.builder()
        		.longOpt("data-file")
        		.hasArg()
        		.type(String.class)
        		.argName("s")
        		.desc("The file that stores the data for this users identities, votes, etc.")
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
        Path settingsPath;
        if (parsedFileOptionValue == null) {
        	settingsPath = Paths.get(System.getProperty("user.home"), ".cweb-settings");
        } else {
        	settingsPath = Paths.get((String) parsedFileOptionValue);
        }
        if (!Files.exists(settingsPath)) {
        	// ok we need to create a user identity here.. no idea how to do that...
        	String pubKey, privKey;
        	String[] lines = {
        		"{",
	        		"\"error_message\": null,",
	        		"\"info_message\": null,",
	        		"\"trusted_identities\": {\""+ pubKey + "\": []},",
					"\"possible_trust_algorithms\": [",
					"    {\"id\": \"EIGENTRUST\", \"name\": \"Eigentrust\"},", 
					"    {\"id\": \"ONLY_FRIENDS\", \"name\": \"Only Friends\"},", 
					"    {\"id\": \"FRIEND_OF_FRIEND\", \"name\": \"Friends of friends\"}",
					"  ],",
					"\"current_trust_algorithm\": {\"id\": \"ONLY_FRIENDS\", \"name\": \"Only Friends\"},",
					"\"current_identity\": {\"name\": \"Default ID\", \"pubKey\": \" "+ pubKey + " \", \"privateKey\": \"" + privKey + "\"}", 
					"\"user_identities\": [{\"name\": \"Default ID\", \"pubKey\": \" "+ pubKey + " \", \"privateKey\": \"" + privKey + "\"}]",
	        		"\"torrent_lists\": {\""+ pubKey + "\": []}",
        		"}"

        	};
        	
			Files.write(settingsPath, Arrays.asList(lines));
        }
        
        // also we need to pass in this path to the Jetty app
        
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
