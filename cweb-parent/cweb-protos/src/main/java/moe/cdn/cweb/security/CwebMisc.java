package moe.cdn.cweb.security;

import java.util.function.BiPredicate;
import java.util.function.Function;

import moe.cdn.cweb.SecurityProtos;
import moe.cdn.cweb.TorrentTrustProtos;

/**
 * Misc constants used for Cweb
 * 
 * @author davix
 */
public final class CwebMisc {
    /**
     * This class should not be instantiated
     */
    private CwebMisc() {}

    public static final Function<SecurityProtos.Hash, CwebId> CWEB_ID_REDUCER =
            hash -> CwebId.fromSha1(hash.toByteArray());

    public static final BiPredicate<SecurityProtos.Hash, TorrentTrustProtos.SignedUser> HASH_SIGNED_USER_BI_PREDICATE =
            (hash, signedUser) -> signedUser.getUser().getPublicKey().getHash().equals(hash);

    public static final BiPredicate<SecurityProtos.Hash, TorrentTrustProtos.SignedVote> HASH_SIGNED_VOTE_BI_PREDICATE =
            (hash, signedVote) -> signedVote.getVote().getContentHash().equals(hash);

    public static final BiPredicate<SecurityProtos.Hash, TorrentTrustProtos.SignedVoteHistory> HASH_SIGNED_VOTE_HISTORY_BI_PREDICATE =
            (hash, signedVoteHistory) -> signedVoteHistory.getHistory().getOwnerPublicKey()
                    .getHash().equals(hash);
}
