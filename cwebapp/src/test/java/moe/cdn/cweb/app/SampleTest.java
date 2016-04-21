package moe.cdn.cweb.app;

import static com.google.common.truth.Truth.assertThat;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class SampleTest extends CwebTest {

    @Test
    public void testApplicationWadlExists() throws Exception {
        assertThat(target().path("application.wadl").request().get().getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);
    }
}
