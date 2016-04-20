package moe.cdn.cweb.app.api.resources;

import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.UserRef;

/**
 * @author davix
 */
@Path("user/trust")
public class UserTrust extends CwebApiEndPoint {

    @POST
    public boolean trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().addUserAsTrusted(publicKey).get();
    }

    @DELETE
    public boolean untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        Hash publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().removeUserAsTrusted(publicKey).get();
    }
}
