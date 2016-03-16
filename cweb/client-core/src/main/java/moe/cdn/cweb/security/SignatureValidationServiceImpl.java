package moe.cdn.cweb.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.security.utils.SignatureUtils;

class SignatureValidationServiceImpl implements SignatureValidationService {

    private final KeyLookupService keyLookupService;

    @Inject
    public SignatureValidationServiceImpl(KeyLookupService keyLookupService) {
        this.keyLookupService = checkNotNull(keyLookupService);
    }

    public boolean validate(byte[] data, Signature signature, User user) {
        switch (signature.getAlgorithm()) {
            case SHA256withRSA:
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
    public boolean validate(byte[] data, Signature signature) {
        Optional<SignedUserRecord> owner = keyLookupService.findOwner(signature.getPublicKey());
        if (!owner.isPresent()) {
            return false;
        }
        return validate(data, signature, owner.get().getUser());
    }

}
