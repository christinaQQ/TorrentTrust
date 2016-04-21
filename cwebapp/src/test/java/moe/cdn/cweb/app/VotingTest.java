package moe.cdn.cweb.app;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.TrustRating;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.dto.UserRef;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.google.common.truth.Truth.assertThat;

public class VotingTest extends CwebTest {
    IdentityMetadata totino;
    IdentityMetadata johnCena;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        totino = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);
        johnCena = target("identity").request().post(
                Entity.json(new UserName("johnCena")), IdentityMetadata.class);

        Response switchToTotino = target("identity/switch").request().post(
                Entity.entity(new UserRef(totino.getPublicKeyHash()),
                        MediaType.APPLICATION_JSON_TYPE));
        assertThat(switchToTotino.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        Response trustJohnCena = target("user/trust").request().post(
                Entity.json(new UserRef(johnCena.getPublicKeyHash())));
        assertThat(trustJohnCena.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        Response switchToJohnCena = target("identity/switch").request().post(
                Entity.json(new UserRef(johnCena.getPublicKeyHash())));
        assertThat(switchToJohnCena.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        Response trustTotino = target("user/trust").request().post(
                Entity.json(new UserRef(totino.getPublicKeyHash())));
        assertThat(trustTotino.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);
    }

    @Test
    public void testUpVoteByOneIdentityIsSeenByOther() throws Exception {
        Response switchToTotino = target("identity/switch").request().post(
                Entity.json(new UserRef(totino.getPublicKeyHash())));
        assertThat(switchToTotino.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        Response upVoteQQQQ = target("object/QQQQ/up").request().post(Entity.json(null));
        assertThat(upVoteQQQQ.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        Response switchToJohnCena = target("identity/switch").request().post(
                Entity.json(new UserRef(johnCena.getPublicKeyHash())));
        assertThat(switchToJohnCena.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        TrustRating rating = target("object/QQQQ/ONLY_FRIENDS").request().get(TrustRating.class);
        assertThat(rating.getAlgorithmName()).isEqualTo("ONLY_FRIENDS");
        assertThat(rating.getRating()).isWithin(1).of(0);
    }
}
