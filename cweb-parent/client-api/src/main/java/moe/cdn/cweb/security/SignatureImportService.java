package moe.cdn.cweb.security;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.concurrent.Future;

/**
 * Persistence for domain objects that need to be cryptographically signed.
 */
public interface SignatureImportService {

    /**
     * Signs the specified data with the specified key pair.
     *
     * @param keyPair the key pair
     * @param data    the data
     * @return the signature for the specified key pair and the specified data.
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    Signature sign(KeyPair keyPair, byte[] data) throws SignatureException, InvalidKeyException;

    /**
     * Imports the specified proto using the specified signature.
     *
     * @param message   the proto
     * @param signature the signature
     * @return {@code true} if the import succeeded
     */
    Future<Boolean> importSignature(Message message, Signature signature);
}
