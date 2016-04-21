package moe.cdn.cweb.app;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

import jersey.repackaged.com.google.common.collect.ImmutableSet;
import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.UserName;
import moe.cdn.cweb.app.dto.UserRef;

public class UserTrustTest extends CwebTest {
    private IdentityMetadata akari;
    private IdentityMetadata kyouko;
    private IdentityMetadata yui;
    private IdentityMetadata chinatsu;

    private void guaranteeIdentities() {
        akari = target("identity").request().post(
                Entity.entity(new UserName("akari"), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        kyouko = target("identity").request().post(
                Entity.entity(new UserName("kyouko"), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        yui = target("identity").request().post(
                Entity.entity(new UserName("yui"), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        chinatsu = target("identity").request().post(
                Entity.entity(new UserName("chinatsu"), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        assertThat(akari).isNotNull();
        assertThat(kyouko).isNotNull();
        assertThat(yui).isNotNull();
        assertThat(chinatsu).isNotNull();
    }

    private void switchTo(IdentityMetadata identity) {
        IdentityMetadata target = target("identity/switch").request().post(Entity
                .entity(new UserRef(identity.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                IdentityMetadata.class);
        assertThat(target).isEqualTo(identity);
    }

    @Test
    public void testCanAddTrust() {
        guaranteeIdentities();
        switchTo(akari);

        boolean success = target("user/trust").request().post(Entity
                .entity(new UserRef(kyouko.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
        assertThat(success).isTrue();
    }

    @Test
    public void testCanAddTrustAndCheckTrust() {
        guaranteeIdentities();
        switchTo(akari);

        boolean success = target("user/trust").request().post(Entity
                .entity(new UserRef(kyouko.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
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
        target("user/trust").request().post(Entity.entity(new UserRef(kyouko.getPublicKeyHash()),
                MediaType.APPLICATION_JSON_TYPE), Boolean.class);
        // delete kyouko
        target("user/trust/delete").request().post(Entity
                .entity(new UserRef(kyouko.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
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
                Entity.entity(new UserRef(yui.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
        // delete kyouko
        target("user/trust/delete").request().post(Entity
                .entity(new UserRef(kyouko.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
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
                Entity.entity(new UserRef(yui.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
        // Add yui again
        target("user/trust").request().post(
                Entity.entity(new UserRef(yui.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
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
        target("user/trust").request().post(Entity.entity(new UserRef(chinatsu.getPublicKeyHash()),
                MediaType.APPLICATION_JSON_TYPE), Boolean.class);
        List<UserRef> trusted =
                target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet()))
                .containsExactly(chinatsu.getPublicKeyHash());
        
        // Delete chinatsu
        target("user/trust/delete").request().post(Entity
                .entity(new UserRef(chinatsu.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
        // Delete chinatsu again
        target("user/trust/delete").request().post(Entity
                .entity(new UserRef(chinatsu.getPublicKeyHash()), MediaType.APPLICATION_JSON_TYPE),
                Boolean.class);
        
        trusted = target("user/trust").request().get(new GenericType<List<UserRef>>() {});
        assertThat(trusted.stream().map(UserRef::getPublicKey).collect(Collectors.toSet())).isEmpty();
    }
}
