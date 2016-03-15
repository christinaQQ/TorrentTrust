package moe.cdn.cweb.security;


import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;

class CwebValidationServiceImpl implements CwebSignatureValidationService {

    private final SignatureValidationService signatureValidationService;

    @Inject
    public CwebValidationServiceImpl(SignatureValidationService signatureValidationService) {
        this.signatureValidationService = checkNotNull(signatureValidationService);
    }

    @Override
    public boolean validate(byte[] data, Signature signature) {
        return signatureValidationService.validate(data, signature);
    }

    @Override
    public boolean validateVote(SignedVote signedVote) {
        return signedVote.getVote().getOwnerPublicKey()
                .equals(signedVote.getSignature().getPublicKey())
                && validate(signedVote.getVote().toByteArray(), signedVote.getSignature());
    }

    @Override
    public boolean validateUser(SignedUserRecord signedUser) {
        return signedUser.getUser().getPublicKey().equals(signedUser.getSignature().getPublicKey())
                && validate(signedUser.getUser().toByteArray(), signedUser.getSignature());
    }

}
