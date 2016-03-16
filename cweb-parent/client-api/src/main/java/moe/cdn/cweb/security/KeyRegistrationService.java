package moe.cdn.cweb.security;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;

public interface KeyRegistrationService {
    /**
     * Registers a key in the key registry
     *
     * @param publickey
     * @returns true if success false if failure
     */
    boolean registerKey(Key publickey);

    /**
     * Creates a new keypair and makes sure that this keypair can be registered.
     *
     * @return a valid keypair
     */
    KeyPair createKeypair();
}
