package moe.cdn.cweb.trust;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import moe.cdn.cweb.dht.CwebMultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.User.TrustAssertion;
import moe.cdn.cweb.dht.security.CwebSignatureValidationService;
import moe.cdn.cweb.dht.security.UserKeyService;
import moe.cdn.cweb.security.utils.Representations;

class CwebTrustNetworkApiImpl implements CwebTrustNetworkApi {

    private static final Logger logger = LogManager.getLogger();
    private final UserKeyService userKeyService;
    private final CwebSignatureValidationService signatureValidationService;

    @Inject
    public CwebTrustNetworkApiImpl(UserKeyService userKeyService,
                                   CwebSignatureValidationService signatureValidationService) {
        this.userKeyService = checkNotNull(userKeyService);
        this.signatureValidationService = checkNotNull(signatureValidationService);
    }

    @Override
    public ListenableFuture<Collection<User>> getLocalTrustNetwork(User user) {
        logger.debug("Getting local trust network for {}", Representations.asString(user));
        LinkedList<ListenableFuture<Optional<User>>> maybeUsers = new LinkedList<>();
        for (TrustAssertion t : user.getTrustedList()) {
            maybeUsers.add(Futures.transform(userKeyService.findOwner(t.getPublicKey()),
                    (Function<Optional<SignedUser>, Optional<User>>) maybeOwner -> maybeOwner.map(
                            o -> signatureValidationService.validateUser(o) ? o.getUser() : null)));
        }
        return Futures.transform(Futures.successfulAsList(maybeUsers),
                (Function<List<Optional<User>>, Collection<User>>) result -> result.stream()
                        .filter(Objects::nonNull).filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toList()));
    }

    @Override
    public ListenableFuture<Boolean> addUserAsTrusted(User user) {
        logger.debug("Adding to trust network: {}", Representations.asString(user));
        return userKeyService.addTrustAssertion(TrustAssertion.newBuilder()
                .setPublicKey(user.getPublicKey())
                .setTrustAssertion(TrustAssertion.Trust.TRUSTED)
                .build());
    }
}
