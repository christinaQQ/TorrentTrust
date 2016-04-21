package moe.cdn.cweb.app;

import com.google.inject.servlet.GuiceFilter;
import moe.cdn.cweb.app.api.CwebApiConfig;
import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.services.CwebApiService;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.inmemory.InMemoryTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.EnumSet;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author davix
 */
public class UsersCanGetRatingTest extends JerseyTest {

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

    @Test
    public void testApplicationWadlExists() throws Exception {
        assertThat(target().path("application.wadl").request().get().getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);
    }

    @Test
    public void test() {
        IdentityMetadata identity = target("identity").request().post(
                Entity.entity(new UserName("totino"), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        assertThat(identity).isNotNull();
    }
}
