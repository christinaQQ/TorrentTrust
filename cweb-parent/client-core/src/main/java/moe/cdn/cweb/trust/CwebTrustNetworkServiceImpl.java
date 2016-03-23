package moe.cdn.cweb.trust;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.User.TrustAssertion;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;
import moe.cdn.cweb.dht.security.KeyLookupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

class CwebTrustNetworkServiceImpl implements CwebTrustNetworkService {

    private static final Logger logger = LogManager.getLogger();
    private final KeyLookupService keyLookupService;
    private final CwebSignatureValidationService signatureValidationService;

    @Inject
    public CwebTrustNetworkServiceImpl(KeyLookupService keyLookupService,
                                       CwebSignatureValidationService signatureValidationService) {
        this.keyLookupService = checkNotNull(keyLookupService);
        this.signatureValidationService = checkNotNull(signatureValidationService);
    }

    @Override
    public Collection<User> getLocalTrustNetwork(User user) {
        LinkedList<ListenableFuture<Optional<User>>> maybeUsers = new LinkedList<>();
        for (TrustAssertion t : user.getTrustedList()) {
            maybeUsers.add(Futures.transform(keyLookupService.findOwner(t.getPublicKey()),
                    (Function<Optional<SignedUser>, Optional<User>>)
                            maybeOwner -> maybeOwner.map(
                                    o -> signatureValidationService.validateUser(o) ? o.getUser()
                                                                                    : null)));
        }
        try {
            return Futures.successfulAsList(maybeUsers).get()
                    .stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors
                            .toList());
        } catch (InterruptedException | ExecutionException e) {
            logger.catching(e);
            return Collections.emptyList();
        }
    }

}
