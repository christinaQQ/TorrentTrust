package moe.cdn.cweb.app.api.resources;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.UserRef;

/**
 * @author davix
 */
@Path("user/trust")
public class UserTrust extends CwebApiEndPoint {

    @GET
    public List<UserRef> getTrustedUsers() throws InterruptedException, ExecutionException {
        Optional<User> currentUser = getCwebTrustNetworkApi().getUserIdentity().get();
        return getCwebTrustNetworkApi().getLocalTrustNetwork(currentUser.get()).get().stream()
                .map(u -> new UserRef(u.getPublicKey().getHash())).collect(Collectors.toList());
    }

    @POST
    public boolean trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().addUserAsTrusted(publicKey).get();
    }

    @POST
    @Path("delete")
    public boolean untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().removeUserAsTrusted(publicKey).get();
    }
}
