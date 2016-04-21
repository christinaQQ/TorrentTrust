package moe.cdn.cweb.app;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.TrustRating;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static com.google.common.truth.Truth.assertThat;

public class VotingTest extends CwebApiTest {
    IdentityMetadata totino;
    IdentityMetadata johnCena;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        totino = newIdentity("totino");
        johnCena = newIdentity("john_cena");

        switchTo(totino);
        trust(johnCena);

        switchTo(johnCena);
        trust(totino);
    }

    @Test
    public void testUpVoteByOneIdentityIsSeenByOther() throws Exception {
        switchTo(totino);

        Response upVoteQQQQ = target("object/QQQQ/up").request().post(Entity.json(null));
        assertThat(upVoteQQQQ.getStatusInfo().getFamily())
                .isEqualTo(Response.Status.Family.SUCCESSFUL);

        switchTo(johnCena);

        TrustRating rating = target("object/QQQQ/ONLY_FRIENDS").request().get(TrustRating.class);
        assertThat(rating.getAlgorithmName()).isEqualTo("ONLY_FRIENDS");
        assertThat(rating.getRating()).isWithin(1).of(0);
    }
}
