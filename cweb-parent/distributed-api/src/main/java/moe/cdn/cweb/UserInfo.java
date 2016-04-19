package moe.cdn.cweb;

/**
 * @author davix
 */
public interface UserInfo {
    String getHandle();
    SecurityProtos.KeyPair getKeyPair();
}
