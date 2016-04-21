package moe.cdn.cweb.app;

import org.junit.Test;

import javax.ws.rs.core.Response;

import static com.google.common.truth.Truth.assertThat;

public class SampleTest extends CwebTest {

    @Test
    public void testApplicationWadlExists() throws Exception {
        assertThat(target().path("application.wadl").request().get().getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);
    }
}
