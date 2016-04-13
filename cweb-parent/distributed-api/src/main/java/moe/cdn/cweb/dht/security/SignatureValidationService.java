package moe.cdn.cweb.dht.security;

import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos;

public interface SignatureValidationService {
    /**
     * Validates that the signature's public key exists in the trust network,
     * the signature is owned by the user, and that the signature signs the
     * message.
     *
     * @param signature the signature protobuf
     * @param message the protobuf to validate
     * @return {@code true} if the message is signed by the signature
     */
    boolean validateAndCheckSignatureKeyInNetwork(Signature signature, Message message);

    /**
     * Affirms that the signature is owned by the user and that the signature
     * signs the message.
     *
     * @param signature the signature protobuf
     * @param user the user that owns the signature
     * @param message the protobuf to validate
     * @return {@code true} if the message is signed by the signature
     */
    boolean validateSelfSigned(Signature signature, TorrentTrustProtos.User user, Message message);

    /**
     * Affirms that the signature is owned by the user and that the signature
     * signs the message.
     *
     * @param signature the signature protobuf
     * @param user the user that owns the signature
     * @param data the data to validate
     * @return {@code true} if the message is signed by the signature
     */
    boolean validateSelfSigned(Signature signature, TorrentTrustProtos.User user, byte[] data);

    /**
     * Validates that the signature's public key exists in the trust network,
     * the signature is owned by the user, and that the signature signs the
     * data.
     *
     * @param signature the signature protobuf
     * @param data the data to validate
     * @return {@code true} if the message is signed by the signature
     */
    boolean validateAndCheckSignatureKeyInNetwork(Signature signature, byte[] data);

}
