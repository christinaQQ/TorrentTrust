package moe.cdn.cweb.app.api;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.app.dto.UserRef;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.trust.CwebTrustNetworkApi;

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
        String publicKeyBase64 = userRef.getPublicKeyBase64();
        byte[] publicKey = Base64.decode(publicKeyBase64);
        CwebTrustNetworkApi cwebTrustNetworkApi = getCwebTrustNetworkApi();
        return cwebTrustNetworkApi
                .addUserAsTrusted(TorrentTrustProtos.User.newBuilder()
                        .setPublicKey(KeyUtils.createPublicKey(publicKey))
                        .build())
                .get();
    }
}
