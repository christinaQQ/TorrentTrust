package moe.cdn.cweb.app.api;

import moe.cdn.cweb.UserInfo;
import moe.cdn.cweb.app.dto.Identity;
import moe.cdn.cweb.app.dto.KeyPairBase64;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Base64;

/**
 * @author davix
 */
@Path("identity")
public class UserIdentity extends CwebApiEndPoint {
    // TODO: should the "current" user really be stored in the backend?

    @GET
    public Identity getCurrentIdentity() {
        UserInfo user = getCwebEnvironment().getUserInfo();
        return new Identity(getCwebEnvironment().getMyId().toBase64String(),
                user.getHandle(),
                new KeyPairBase64(
                        Base64.getEncoder().encodeToString(user.getKeyPair().getPublicKey()
                                .toByteArray()),
                        Base64.getEncoder().encodeToString(user.getKeyPair().getPrivateKey()
                                .toByteArray())));
    }
}
