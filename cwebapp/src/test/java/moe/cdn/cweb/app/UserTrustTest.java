package moe.cdn.cweb.app;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserRef;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class UserTrustTest extends CwebApiTest {
    private IdentityMetadata akari;
    private IdentityMetadata kyouko;
    private IdentityMetadata yui;
    private IdentityMetadata chinatsu;

    private void guaranteeIdentities() {
        akari = newIdentity("akari");
        kyouko = newIdentity("kyouko");
        yui = newIdentity("yui");
        chinatsu = newIdentity("chinatsu");
        assertThat(akari).isNotNull();
        assertThat(kyouko).isNotNull();
        assertThat(yui).isNotNull();
        assertThat(chinatsu).isNotNull();
    }

    @Test
    public void testCanAddTrust() {
        guaranteeIdentities();
        switchTo(akari);

        Response r = target("user/trust").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())));
        assertThat(r.getStatusInfo().getFamily()).isEqualTo(Response.Status.Family.SUCCESSFUL);
    }

    @Test
    public void testCanAddTrustAndCheckTrust() {
        guaranteeIdentities();
        switchTo(akari);

        Response r = target("user/trust").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())));
        assertThat(r.getStatusInfo().getFamily()).isEqualTo(Response.Status.Family.SUCCESSFUL);
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(kyouko.getPublicKeyHash());
    }

    @Test
    public void testCanAddTrustAndRemoveTrust() {
        guaranteeIdentities();
        switchTo(akari);

        // Add kyouko
        target("user/trust").request().post(Entity.json(new UserRef(kyouko.getPublicKeyHash())));
        // delete kyouko
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())));
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});

        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .isEmpty();
    }

    @Test
    public void testDeleteNonExistent() {
        guaranteeIdentities();
        switchTo(akari);

        // Add yui
        target("user/trust").request().post(
                Entity.json(new UserRef(yui.getPublicKeyHash())));
        // delete kyouko
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())));
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});

        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(yui.getPublicKeyHash());
    }

    @Test
    public void testAddTwice() {
        guaranteeIdentities();
        switchTo(akari);

        // Add yui
        target("user/trust").request().post(
                Entity.json(new UserRef(yui.getPublicKeyHash())));
        // Add yui again
        target("user/trust").request().post(
                Entity.json(new UserRef(yui.getPublicKeyHash())),
                Boolean.class);
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});

        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(yui.getPublicKeyHash());
    }

    @Test
    public void testDeleteTwice() {
        guaranteeIdentities();
        switchTo(akari);

        // Add chinatsu
        target("user/trust").request().post(Entity.json(new UserRef(chinatsu.getPublicKeyHash())));
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(chinatsu.getPublicKeyHash());
        
        // Delete chinatsu
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(chinatsu.getPublicKeyHash())));
        // Delete chinatsu again
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(chinatsu.getPublicKeyHash())));
        
        trusted = target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet())).isEmpty();
    }
}
