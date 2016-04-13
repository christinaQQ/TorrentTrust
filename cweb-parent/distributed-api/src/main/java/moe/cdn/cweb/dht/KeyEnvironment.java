package moe.cdn.cweb.dht;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;

public interface KeyEnvironment {
    String getHandle();
    KeyPair getKeyPair();
    User getLocalUser();
}
