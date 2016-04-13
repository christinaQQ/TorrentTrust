package moe.cdn.cweb.app.api;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.app.dto.KeyPair;
import moe.cdn.cweb.security.utils.KeyUtils;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Base64;

/**
 * @author davix
 */
@Path("generate_key_pair")
public class KeyPairGenerator {
    @POST
    public KeyPair generateKeyPair() {
        SecurityProtos.KeyPair protoKeyPair = KeyUtils.generateKeyPair();
        return new KeyPair(
                Base64.getEncoder().encodeToString(protoKeyPair.getPublicKey().getRaw()
                        .toByteArray()),
                Base64.getEncoder().encodeToString(protoKeyPair.getPrivateKey().getRaw()
                        .toByteArray()));
    }
}
