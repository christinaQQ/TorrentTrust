package moe.cdn.cweb.app.api;

import moe.cdn.cweb.app.data.State;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author davix
 */
@Path("test")
public class Test {
    @GET
    public State test() {
        return new State(new byte[] {17, 17, 17, 17});
    }
}
