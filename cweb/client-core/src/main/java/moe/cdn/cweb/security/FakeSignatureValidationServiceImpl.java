package moe.cdn.cweb.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;

public class FakeSignatureValidationServiceImpl implements SignatureValidationService {

    private final KeyLookupService keyLookupService;

    public FakeSignatureValidationServiceImpl(KeyLookupService keyLookupService) {
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
                    X509EncodedKeySpec publicKeySpec =
                            new X509EncodedKeySpec(signature.getPublicKey().getRaw().toByteArray());
                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    verifier.initVerify(kf.generatePublic(publicKeySpec));
                    verifier.update(data);
                    return verifier.verify(signature.getSignature().toByteArray());
                } catch (NoSuchAlgorithmException e) {
                    throw new UnsupportedAlgorithmException();
                } catch (InvalidKeyException e) {
                    throw new RuntimeException("Key decoding failed.", e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException("Key spec failed", e);
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
