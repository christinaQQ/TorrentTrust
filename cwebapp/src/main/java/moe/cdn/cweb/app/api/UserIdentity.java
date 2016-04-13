package moe.cdn.cweb.app.api;

import moe.cdn.cweb.app.dto.Identity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author davix
 */
@Path("identity")
public class UserIdentity {
    // TODO: should the "current" user really be stored in the backend? Single-user model??

    @GET
    public Identity getAllProfiles() {
        return new Identity();
    }

}
