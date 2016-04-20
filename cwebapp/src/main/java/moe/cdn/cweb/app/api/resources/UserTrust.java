package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.api.CwebApiEndPoint;
import moe.cdn.cweb.app.dto.UserRef;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.concurrent.ExecutionException;

/**
 * @author davix
 */
@Path("user/trust")
public class UserTrust extends CwebApiEndPoint {

    @POST
    public boolean trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        SecurityProtos.Key publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().addUserAsTrusted(publicKey).get();
    }

    @DELETE
    public boolean untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        SecurityProtos.Key publicKey = userRef.getPublicKey();
        return getCwebTrustNetworkApi().removeUserAsTrusted(publicKey).get();
    }
}
