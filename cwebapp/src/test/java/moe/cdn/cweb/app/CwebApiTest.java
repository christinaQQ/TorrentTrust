package moe.cdn.cweb.app;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.dto.UserRef;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by David on 4/20/2016.
 */
public class CwebApiTest extends CwebTest {
    protected IdentityMetadata newIdentity(String handle) {
        IdentityMetadata identity = target("identity").request().post(
                Entity.json(new UserName(handle)), IdentityMetadata.class);
        assertThat(identity.getHandle()).isEqualTo(handle);
        return identity;
    }

    protected void switchTo(IdentityMetadata identity) {
        IdentityMetadata target = target("identity/switch").request().post(
                Entity.json(new UserRef(identity.getPublicKeyHash())), IdentityMetadata.class);
        assertThat(target).isEqualTo(identity);
    }

    protected void trust(IdentityMetadata identity) {
        Response r = target("user/trust").request().post(
                Entity.json(new UserRef(identity.getPublicKeyHash())));
        assertThat(r.getStatusInfo().getFamily()).isEqualTo(Response.Status.Family.SUCCESSFUL);
    }
}
