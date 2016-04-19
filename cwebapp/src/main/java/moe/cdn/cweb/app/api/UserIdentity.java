package moe.cdn.cweb.app.api;

import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.exceptions.NoSuchUserException;
import moe.cdn.cweb.app.dto.Identity;
import moe.cdn.cweb.app.dto.KeyPairBase64;

/**
 * @author davix
 */
@Path("identity")
public class UserIdentity extends CwebApiEndPoint {
    // TODO: should the "current" user really be stored in the backend?

    @GET
    public Identity getCurrentIdentity() throws InterruptedException, ExecutionException {
        KeyPair keyPair = getCwebEnvironment().getKeyPair();
        Optional<User> current = getCwebTrustNetworkApi().getUserIdentity().get();
        if (!current.isPresent()) {
            // TODO
            throw new NoSuchUserException(keyPair.getPublicKey());
        }
        return new Identity(getCwebEnvironment().getMyId().toBase64String(),
                current.get().getHandle(),
                new KeyPairBase64(
                        Base64.getEncoder().encodeToString(keyPair.getPublicKey().toByteArray()),
                        Base64.getEncoder().encodeToString(keyPair.getPrivateKey().toByteArray())));
    }
}
