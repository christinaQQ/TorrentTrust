package moe.cdn.cweb.dht.security;


import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;

import javax.inject.Inject;

class CwebValidationServiceImpl extends SignatureValidationServiceImpl
        implements CwebSignatureValidationService {

    @Inject
    public CwebValidationServiceImpl(KeyLookupService keyLookupService) {
        super(keyLookupService);
    }

    @Override
    public boolean validateVote(SignedVote signedVote) {
        // must check that the owner of the vote is owner of the signature
        return signedVote.getVote().getOwnerPublicKey().equals(signedVote.getSignature()
                .getPublicKey())
                && validateAndCheckSignatureKeyInNetwork(signedVote.getSignature(), signedVote
                .getVote());
    }

    @Override
    public boolean validateUser(SignedUser signedUser) {
        return validateSelfSigned(signedUser.getSignature(), signedUser.getUser(), signedUser
                .getUser());
    }

}
