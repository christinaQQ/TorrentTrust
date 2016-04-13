package moe.cdn.cweb.app.api;

import moe.cdn.cweb.app.dto.UserRef;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

/**
 * @author davix
 */
@Path("user/trust")
public class UserTrust extends CwebApiEndPoint {

    @POST
    public boolean trustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        String publicKeyBase64 = userRef.getPublicKeyBase64();
        byte[] publicKey = Base64.getDecoder().decode(publicKeyBase64);
        CwebTrustNetworkApi cwebTrustNetworkApi = getCwebTrustNetworkApi();
        return cwebTrustNetworkApi.addUserAsTrusted(KeyUtils.createPublicKey(publicKey)).get();
    }

    @DELETE
    public boolean untrustUser(UserRef userRef) throws ExecutionException, InterruptedException {
        String publicKeyBase64 = userRef.getPublicKeyBase64();
        byte[] publicKey = Base64.getDecoder().decode(publicKeyBase64);
        return getCwebTrustNetworkApi().removeUserAsTrusted(KeyUtils.createPublicKey(publicKey))
                .get();
    }
}
