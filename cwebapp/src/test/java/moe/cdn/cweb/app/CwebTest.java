package moe.cdn.cweb.app;

import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import moe.cdn.cweb.app.api.CwebApiConfig;
import moe.cdn.cweb.app.services.CwebApiService;

/**
 * @author davix
 */
public abstract class CwebTest extends JerseyTest {
    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {
        ServletDeploymentContext deploymentContext = ServletDeploymentContext.forServlet(
                new ServletContainer(new CwebApiConfig()))
//                .addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class))
                .addListener(CwebGuiceServletConfig.class)
                .addListener(CwebApiService.class)
                .build();
        deploymentContext.getResourceConfig();
        return deploymentContext;
    }
}
