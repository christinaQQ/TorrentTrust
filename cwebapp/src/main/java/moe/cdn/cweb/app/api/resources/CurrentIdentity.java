package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.api.exceptions.NoSuchUserException;
import moe.cdn.cweb.app.dto.IdentityMetadata;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Path("me")
public class CurrentIdentity extends CwebApiEndPoint {
    
    @GET
    public IdentityMetadata getCurrentIdentity() throws InterruptedException, ExecutionException {
        KeyPair keyPair = getCwebEnvironment().getKeyPair();
        Optional<User> current = getCwebTrustNetworkApi().getUserIdentity().get();
        if (!current.isPresent()) {
            throw new NoSuchUserException(keyPair.getPublicKey());
        }
        return new IdentityMetadata(current.get().getHandle(), keyPair);
    }
}
