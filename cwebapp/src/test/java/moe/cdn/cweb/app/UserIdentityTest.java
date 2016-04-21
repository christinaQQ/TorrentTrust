package moe.cdn.cweb.app;

import static com.google.common.truth.Truth.assertThat;

import javax.ws.rs.client.Entity;

import org.junit.Test;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.dto.UserRef;

/**
 * @author davix
 */
public class UserIdentityTest extends CwebTest {

    @Test
    public void testIdentityExists() {
        IdentityMetadata identity = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);
        assertThat(identity).isNotNull();
    }

    @Test
    public void testIdentityHasFields() {
        IdentityMetadata identity = target("identity").request().post(
                Entity.json(new UserName("totino")),
                IdentityMetadata.class);
        assertThat(identity.getHandle()).isEqualTo("totino");
        assertThat(identity.getPrivateKeyHash()).isNotNull();
        assertThat(identity.getPublicKeyHash()).isNotNull();
    }

    @Test
    public void testDuplicateIdentity() {
        IdentityMetadata original = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);
        IdentityMetadata duplicate = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);

        assertThat(original).isNotNull();
        assertThat(duplicate).isNotNull();

        assertThat(original.getHandle()).isEqualTo("totino");
        assertThat(duplicate.getHandle()).isEqualTo("totino");

        assertThat(original.getPrivateKeyHash()).isNotNull();
        assertThat(original.getPublicKeyHash()).isNotNull();
        assertThat(duplicate.getPrivateKeyHash()).isNotNull();
        assertThat(duplicate.getPublicKeyHash()).isNotNull();

        assertThat(original.getPublicKeyHash()).isNotEqualTo(duplicate.getPublicKeyHash());
        assertThat(original.getPrivateKeyHash()).isNotEqualTo(duplicate.getPrivateKeyHash());
    }

    @Test
    public void testSwitchSameIdentity() throws Exception {
        IdentityMetadata totino = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);
        IdentityMetadata totino2 = target("identity/switch").request().post(
                Entity.json(new UserRef(totino.getPublicKeyHash())), IdentityMetadata.class);
        assertThat(totino).isEqualTo(totino2);
    }

    @Test
    public void testSwitchIdentity() throws Exception {
        IdentityMetadata totino = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);
        IdentityMetadata johnCena = target("identity").request().post(
                Entity.json(new UserName("johncena")), IdentityMetadata.class);

        IdentityMetadata totino2 = target("identity/switch").request().post(
                Entity.json(new UserRef(totino.getPublicKeyHash())), IdentityMetadata.class);
        assertThat(totino).isEqualTo(totino2);


        IdentityMetadata johnCena2 = target("identity/switch").request().post(
                Entity.json(new UserRef(johnCena.getPublicKeyHash())), IdentityMetadata.class);
        assertThat(johnCena).isEqualTo(johnCena2);
    }

    @Test
    public void testSwitchIdentityCurrent() throws Exception {
        IdentityMetadata totino = target("identity").request().post(
                Entity.json(new UserName("totino")), IdentityMetadata.class);

        target("identity/switch").request().post(
                Entity.json(new UserRef(totino.getPublicKeyHash())), IdentityMetadata.class);

        IdentityMetadata me = target("me").request().get(IdentityMetadata.class);
        assertThat(me).isEqualTo(totino);
    }
}
