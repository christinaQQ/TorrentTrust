package moe.cdn.cweb.security;

import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;

public interface SignatureImportService {
    /**
     * Signs bytedata and produces a signature
     * 
     * @param data
     * @param privatekey
     */
    Signature sign(byte[] data, KeyPair keypair);

    /**
     * Imports the signature into the network
     * 
     * @param signature
     * @return
     */
    boolean importSignature(Message message, Signature signature);
}
