package moe.cdn.cweb.dht;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;

// TODO: Should the "current" user's environment be implicit (depends on
// injection and context)??
public interface KeyEnvironment {
    String getHandle();

    KeyPair getKeyPair();

    User getLocalUser();
}
