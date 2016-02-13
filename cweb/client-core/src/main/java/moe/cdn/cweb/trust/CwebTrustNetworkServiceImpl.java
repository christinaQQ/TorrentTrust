package moe.cdn.cweb.trust;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

import com.google.inject.Inject;

import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.User.TrustAssertion;
import moe.cdn.cweb.security.CwebSignatureValidationService;
import moe.cdn.cweb.security.KeyLookupService;

class CwebTrustNetworkServiceImpl implements CwebTrustNetworkService {

    private final KeyLookupService keyLookupService;
    private final CwebSignatureValidationService signatureValidationService;

    @Inject
    public CwebTrustNetworkServiceImpl(KeyLookupService keyLookupService,
            CwebSignatureValidationService signatureValidationService) {
        this.keyLookupService = keyLookupService;
        this.signatureValidationService = signatureValidationService;
    }

    @Override
    public Collection<User> getLocalTrustNetwork(User user) {
        LinkedList<User> users = new LinkedList<>();
        for (TrustAssertion t : user.getTrustedList()) {
            Optional<SignedUserRecord> owner = keyLookupService.findOwner(t.getPublicKey());
            if (owner.isPresent()) {
                if (signatureValidationService.validateUser(owner.get())) {
                    users.add(owner.get().getUser());
                }
            }
        }
        return users;
    }

}
