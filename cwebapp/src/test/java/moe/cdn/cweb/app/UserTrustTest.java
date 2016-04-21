package moe.cdn.cweb.app;

import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.dto.UserRef;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

public class UserTrustTest extends CwebTest {
    private IdentityMetadata akari;
    private IdentityMetadata kyouko;
    private IdentityMetadata yui;
    private IdentityMetadata chinatsu;

    private void guaranteeIdentities() {
        akari = target("identity").request().post(
                Entity.json(new UserName("akari")),
                IdentityMetadata.class);
        kyouko = target("identity").request().post(
                Entity.json(new UserName("kyouko")),
                IdentityMetadata.class);
        yui = target("identity").request().post(
                Entity.json(new UserName("yui")),
                IdentityMetadata.class);
        chinatsu = target("identity").request().post(
                Entity.json(new UserName("chinatsu")),
                IdentityMetadata.class);
        assertThat(akari).isNotNull();
        assertThat(kyouko).isNotNull();
        assertThat(yui).isNotNull();
        assertThat(chinatsu).isNotNull();
    }

    private void switchTo(IdentityMetadata identity) {
        IdentityMetadata target = target("identity/switch").request().post(
                Entity.json(new UserRef(identity.getPublicKeyHash())), IdentityMetadata.class);
        assertThat(target).isEqualTo(identity);
    }

    @Test
    public void testCanAddTrust() {
        guaranteeIdentities();
        switchTo(akari);

        boolean success = target("user/trust").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())),
                Boolean.class);
        assertThat(success).isTrue();
    }

    @Test
    public void testCanAddTrustAndCheckTrust() {
        guaranteeIdentities();
        switchTo(akari);

        boolean success = target("user/trust").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())), Boolean.class);
        assertThat(success).isTrue();
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
        target("user/trust").request().post(Entity.json(new UserRef(kyouko.getPublicKeyHash())),
                Boolean.class);
        // delete kyouko
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())),
                Boolean.class);
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
                Entity.json(new UserRef(yui.getPublicKeyHash())),
                Boolean.class);
        // delete kyouko
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(kyouko.getPublicKeyHash())), Boolean.class);
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
                Entity.json(new UserRef(yui.getPublicKeyHash())),
                Boolean.class);
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
        target("user/trust").request().post(Entity.json(new UserRef(chinatsu.getPublicKeyHash())),
                Boolean.class);
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(chinatsu.getPublicKeyHash());
        
        // Delete chinatsu
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(chinatsu.getPublicKeyHash())), Boolean.class);
        // Delete chinatsu again
        target("user/trust/delete").request().post(
                Entity.json(new UserRef(chinatsu.getPublicKeyHash())), Boolean.class);
        
        trusted = target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet())).isEmpty();
    }
}
