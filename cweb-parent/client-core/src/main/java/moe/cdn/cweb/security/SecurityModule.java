package moe.cdn.cweb.security;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;

import javax.inject.Singleton;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class SecurityModule extends AbstractModule {

    private static final Function<Hash, CwebId> BIG_INTEGER_REDUCER =
            hash -> new CwebId(HashUtils.sha1(hash.toByteArray()));

    private static final BiPredicate<Hash, SignedUser> HASH_SIGNED_USER_BI_PREDICATE =
            (hash, signedUser) -> signedUser.getUser().getPublicKey().getHash().equals(hash);

    private static final BiPredicate<Hash, SignedVote> HASH_SIGNED_VOTE_BI_PREDICATE =
            (hash, signedVote) -> signedVote.getVote().getContentHash().equals(hash);

    // FIXME this should be injected at a different level
    @Provides
    static CwebMap<SignedUser> provideHashSignedUserCwebMap(
            CwebMapFactory<SignedUser> cwebMapFactory) {
        return cwebMapFactory.create(BIG_INTEGER_REDUCER, HASH_SIGNED_USER_BI_PREDICATE);
    }

    @Provides
    static CwebMap<SignedVote> provideHashSignedVoteCwebMap(
            CwebMapFactory<SignedVote> cwebMapFactory) {
        return cwebMapFactory.create(BIG_INTEGER_REDUCER, HASH_SIGNED_VOTE_BI_PREDICATE);
    }

    @Provides
    @Singleton
    static KeyPair provideKeyPair() {
        // TODO: Fix key pair generation! Implement retrieval of a key pair from the environment
        return KeyUtils.generateKeyPair();
    }

    @Override
    protected void configure() {
        bind(CwebImportService.class).to(CwebImportServiceImpl.class).in(Singleton.class);
    }

}
