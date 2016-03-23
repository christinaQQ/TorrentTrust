package moe.cdn.cweb.security;

import moe.cdn.cweb.SecurityProtos.KeyPair;

public interface KeyEnviroment {
    KeyPair getKeyPair();
}
