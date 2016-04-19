package moe.cdn.cweb.app;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.dto.UserRef;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author davix
 */
@Path("test")
public class Test {
    @GET
    public UserRef test() {
        return new UserRef(SecurityProtos.Key.newBuilder().build());
    }
}
