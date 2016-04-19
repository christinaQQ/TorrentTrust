package moe.cdn.cweb.dht;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.UserInfo;

public interface KeyEnvironment {
    String getHandle();

    KeyPair getKeyPair();

     UserInfo getUserInfo();
}
