package moe.cdn.cweb.app.api.resources;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.dto.KeyPairBase64;
import moe.cdn.cweb.security.utils.KeyUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author davix
 */
@Path("generate_key_pair")
public class KeyPairGenerator {
    @POST
    public KeyPairBase64 generateKeyPair() {
        SecurityProtos.KeyPair protoKeyPair = KeyUtils.generateKeyPair();
        return KeyPairBase64.fromKeyPair(protoKeyPair);
    }
}
