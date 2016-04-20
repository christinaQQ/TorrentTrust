package moe.cdn.cweb.dht;

import moe.cdn.cweb.SecurityProtos.KeyPair;

public interface KeyEnvironment extends Iterable<KeyPair> {
    /**
     * Returns the current {@link KeyPair} being used.
     * 
     * @return key pair being used
     * @throws NoSuchElementException if no key pair is default
     */
    KeyPair getKeyPair();
}
