package moe.cdn.cweb.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Optional;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.security.utils.KeyUtils;

class SignatureValidationServiceImpl implements SignatureValidationService {

    private final KeyLookupService keyLookupService;

    @Inject
    public SignatureValidationServiceImpl(KeyLookupService keyLookupService) {
        this.keyLookupService = keyLookupService;
    }

    public boolean validate(byte[] data, Signature signature, User user) {
        switch (signature.getAlgorithm()) {
            case SHA256withRSA:
                if (!user.getPublicKey().equals(signature.getPublicKey())) {
                    return false;
                }
                // Validate the signature
                try {
                    java.security.Signature verifier =
                            java.security.Signature.getInstance("SHA256withRSA");
                   
                    verifier.initVerify(KeyUtils.importPublicKey(signature.getPublicKey()));
                    verifier.update(data);
                    return verifier.verify(signature.getSignature().toByteArray());
                } catch (NoSuchAlgorithmException e) {
                    throw new UnsupportedAlgorithmException();
                } catch (InvalidKeyException e) {
                    throw new RuntimeException("Key decoding failed.", e);
                } catch (SignatureException e) {
                    throw new RuntimeException("Lol What's a SignatureException", e);
                }
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
