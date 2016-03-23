package moe.cdn.cweb.security;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.security.utils.HashUtils;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * @author davix
 */
public final class CwebMisc {
    public static final Function<SecurityProtos.Hash, CwebId> BIG_INTEGER_REDUCER =
            hash -> new CwebId(HashUtils.sha1(hash.toByteArray()));

    public static final BiPredicate<SecurityProtos.Hash, TorrentTrustProtos.SignedUser>
            HASH_SIGNED_USER_BI_PREDICATE =
            (hash, signedUser) -> signedUser.getUser().getPublicKey().getHash().equals(hash);

    public static final BiPredicate<SecurityProtos.Hash, TorrentTrustProtos.SignedVote>
            HASH_SIGNED_VOTE_BI_PREDICATE =
            (hash, signedVote) -> signedVote.getVote().getContentHash().equals(hash);

    private CwebMisc() {
    }
}
