package moe.cdn.cweb.security;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static moe.cdn.cweb.TorrentTrustProtos.SignedUser;

class SignatureValidationServiceImpl implements SignatureValidationService {

    private static final Logger logger = LogManager.getLogger();
    private final KeyLookupService keyLookupService;

    @Inject
    public SignatureValidationServiceImpl(KeyLookupService keyLookupService) {
        this.keyLookupService = checkNotNull(keyLookupService);
    }

    @Override
    public boolean validateSelfSigned(Signature signature, User user, Message message) {
        return validateSelfSigned(signature, user, message.toByteArray());
    }

    @Override
    public boolean validateSelfSigned(Signature signature, User user, byte[] data) {
        switch (signature.getAlgorithm()) {
            case SHA256withRSA:
                // Check matching public keys
                if (!user.getPublicKey().equals(signature.getPublicKey())) {
                    return false;
                }
                // Validate the signature
                return SignatureUtils.validateMessage(signature, data);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedAlgorithmException();
        }

    }

    @Override
    public boolean validateAndCheckSignatureKeyInNetwork(Signature signature, byte[] data) {
        ListenableFuture<Optional<SignedUser>> futureOwner = keyLookupService.findOwner(signature
                .getPublicKey());
        Optional<SignedUser> owner;
        try {
            owner = futureOwner.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.catching(e);
            return false;
        }
        if (!owner.isPresent()) {
            return false;
        }
        return validateSelfSigned(signature, owner.get().getUser(), data);
    }

    @Override
    public boolean validateAndCheckSignatureKeyInNetwork(Signature signature, Message message) {
        return validateAndCheckSignatureKeyInNetwork(signature, message.toByteArray());
    }

}
