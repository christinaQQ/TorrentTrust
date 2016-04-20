package moe.cdn.cweb.app.api.resources;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.api.exceptions.KeyPairConflictException;
import moe.cdn.cweb.app.dto.IdentityMetadata;
import moe.cdn.cweb.app.dto.KeyHash;
import moe.cdn.cweb.app.dto.UserName;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author davix
 */
@Path("identity")
public class UserIdentity extends CwebApiEndPoint {
    // TODO: should the "current" user really be stored in the backend?

    @POST
    public KeyHash newIdentity(UserName userName) throws InterruptedException, ExecutionException {
        Optional<KeyPair> keyPair =
                getCwebTrustNetworkApi().registerNewUserIdentity(userName.getName()).get();

        if (keyPair.isPresent()) {
            return KeyHash.fromKeyPair(keyPair.get());
        } else {
            throw new KeyPairConflictException();
        }
    }

    private ListenableFuture<Optional<IdentityMetadata>> registerExistingIdentity(String handle,
                                                                                  KeyPair keyPair) {
        return Futures.transform(getCwebIdentityApi().registerExistingUserIdentity(handle, keyPair),
                (Function<Boolean, Optional<IdentityMetadata>>) ok -> {
                    if (ok) {
                        return Optional.of(new IdentityMetadata(handle, keyPair));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    private Function<Optional<User>, ListenableFuture<Optional<IdentityMetadata>>>
    findUserIdentity(KeyPair keyPair) {
        return u -> {
            if (u.isPresent()) {
                return Futures.immediateFuture(Optional.of(
                        new IdentityMetadata(u.get().getHandle(),
                                keyPair)));
            } else {
                String configuredHandle = getCwebIdentities()
                        .getConfiguredHandle(keyPair);
                return registerExistingIdentity(
                        configuredHandle, keyPair);
            }
        };
    }

    @GET
    public List<IdentityMetadata> getAllIdentities()
            throws InterruptedException, ExecutionException {
        FluentIterable<ListenableFuture<Optional<IdentityMetadata>>> futures = FluentIterable.from(
                getCwebIdentities()).transform(keyPair -> Futures.dereference(
                Futures.transform(getCwebIdentityApi().getUserIdentity(keyPair.getPublicKey()),
                        findUserIdentity(keyPair))));
        ListenableFuture<List<Optional<IdentityMetadata>>> maybeIdentityMetadatasFuture =
                Futures.allAsList(futures);
        return maybeIdentityMetadatasFuture.get().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
