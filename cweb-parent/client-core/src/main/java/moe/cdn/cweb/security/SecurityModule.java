package moe.cdn.cweb.security;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.*;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;

import javax.inject.Singleton;
import java.math.BigInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class SecurityModule extends AbstractModule {

    private static final Function<Hash, BigInteger> BIG_INTEGER_REDUCER =
            hash -> new BigInteger(HashUtils.sha1(hash.toByteArray()));

    private static final BiPredicate<Hash, SignedUser> HASH_SIGNED_USER_BI_PREDICATE =
            (hash, signedUser) -> signedUser.getUser().getPublicKey().getHash().equals(hash);

    private static final BiPredicate<Hash, SignedVote> HASH_SIGNED_VOTE_BI_PREDICATE =
            (hash, signedVote) -> signedVote.getVote().getContentHash().equals(hash);

    @Provides
    @UserDomain
    static CwebMap<Hash, SignedUser> provideHashSignedUserCwebMap(DhtNode<SignedUser> dhtNode) {
        return new CwebMapImpl<>(dhtNode, BIG_INTEGER_REDUCER, HASH_SIGNED_USER_BI_PREDICATE);
    }

    @Provides
    @VoteDomain
    static CwebMap<Hash, SignedVote> provideHashSignedVoteCwebMap(DhtNode<SignedVote> dhtNode) {
        return new CwebMapImpl<>(dhtNode, BIG_INTEGER_REDUCER, HASH_SIGNED_VOTE_BI_PREDICATE);
    }

    @Provides
    @Singleton
    static KeyPair provideKeyPair() {
        // TODO: Fix key pair generation! Implement retrieval of a key pair from the environment
        return KeyUtils.generateKeyPair();
    }

    @Override
    protected void configure() {
        bind(KeyLookupService.class).to(KeyLookupServiceImpl.class).in(Singleton.class);
        bind(SignatureValidationService.class).to(SignatureValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebSignatureValidationService.class).to(CwebValidationServiceImpl.class)
                .in(Singleton.class);
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
