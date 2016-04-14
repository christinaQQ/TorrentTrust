package moe.cdn.cweb.security;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.SignedVoteHistory;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.TorrentTrustProtos.VoteHistory;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.dht.annotations.VoteHistoryDomain;
import moe.cdn.cweb.security.utils.Representations;
import moe.cdn.cweb.security.utils.SignatureUtils;

public class CwebImportServiceImpl implements CwebImportService {
    private static final Logger logger = LogManager.getLogger();

    private final KeyEnvironment keyEnvironment;
    private final CwebMultiMap<SignedUser> userMap;
    private final CwebMultiMap<SignedVote> voteMap;
    private final CwebMultiMap<SignedVoteHistory> voteHistoryMap;

    @Inject
    public CwebImportServiceImpl(KeyEnvironment keyEnvironment,
            @UserDomain CwebMultiMap<SignedUser> userMap,
            @VoteDomain CwebMultiMap<SignedVote> voteMap,
            @VoteHistoryDomain CwebMultiMap<SignedVoteHistory> voteHistoryMap) {

        this.keyEnvironment = checkNotNull(keyEnvironment);
        this.userMap = checkNotNull(userMap);
        this.voteMap = checkNotNull(voteMap);
        this.voteHistoryMap = checkNotNull(voteHistoryMap);
    }

    /**
     * Ensures that a {@link VoteHistory} can be obtained
     *
     * @param userPublicKey
     * @return
     */
    private ListenableFuture<VoteHistory> ensureVoteHistory(Key userPublicKey) {
        return Futures.transform(voteHistoryMap.get(userPublicKey.getHash()),
                (Function<SignedVoteHistory, VoteHistory>) history -> history == null
                        ? VoteHistory.newBuilder().setOwnerPublicKey(userPublicKey).build()
                        : history.getHistory());
    }

    @Override
    public Signature sign(KeyPair keyPair, byte[] data)
            throws SignatureException, InvalidKeyException {
        return SignatureUtils.signMessage(keyPair, data);
    }

    /**
     * Produces a signature using the user's keypair
     *
     * @param message
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     */
    private Signature sign(Message message) throws InvalidKeyException, SignatureException {
        return sign(keyEnvironment.getKeyPair(), message.toByteArray());
    }

    @Override
    public synchronized ListenableFuture<Boolean> updateVote(Vote vote)
            throws SignatureException, InvalidKeyException {
        // Get existing vote history and add content hash if necessary
        ListenableFuture<VoteHistory> amendedHistory =
                Futures.transform(ensureVoteHistory(vote.getOwnerPublicKey()),
                        (Function<VoteHistory, VoteHistory>) history -> {
                            // FIXME: should correctly update a vote on a
                            // duplicate property
                            if (history.getContentHashList().contains(vote.getContentHash())) {
                                return history;
                            } else {
                                return history.toBuilder().addContentHash(vote.getContentHash())
                                        .build();
                            }
                        });
        // Write the history
        ListenableFuture<Boolean> voteHistoryFuture = Futures.transform(amendedHistory,
                (AsyncFunction<VoteHistory, Boolean>) voteHistory -> importSignature(voteHistory,
                        sign(voteHistory)));
        return Futures
                .transform(voteHistoryFuture,
                        (AsyncFunction<Boolean, Boolean>) success -> success
                                ? importSignature(vote, sign(vote))
                                : Futures.immediateFuture(false));
    }

    @Override
    public ListenableFuture<Boolean> importUser(User user)
            throws SignatureException, InvalidKeyException {
        return importSignature(user, sign(user));
    }

    @Override
    public Future<Boolean> importTrustAssertion(User.TrustAssertion trustAssertion) {
        return Futures.transform(userMap.get(keyEnvironment.getKeyPair().getPublicKey().getHash()),
                (AsyncFunction<SignedUser, Boolean>) user -> {
                    User u = user.getUser();
                    u.getTrustedList().add(trustAssertion);
                    return importUser(u);
                });
    }

    @Override
    public ListenableFuture<Boolean> importSignature(Vote vote, Signature signature) {
        SignedVote signedVote =
                SignedVote.newBuilder().setSignature(signature).setVote(vote).build();
        logger.info("Importing vote {} (signature: {})", Representations.asString(vote),
                Representations.asString(signature));
        return voteMap.add(signedVote.getVote().getContentHash(), signedVote);
    }

    @Override
    public ListenableFuture<Boolean> importSignature(User user, Signature signature) {
        SignedUser signedUser =
                SignedUser.newBuilder().setSignature(signature).setUser(user).build();
        logger.info("Importing user {} (signature: {})", Representations.asString(user),
                Representations.asString(signature));
        return userMap.put(signedUser.getUser().getPublicKey().getHash(), signedUser);
    }

    @Override
    public ListenableFuture<Boolean> importSignature(VoteHistory voteHistory, Signature signature) {
        SignedVoteHistory signedVoteHistory = SignedVoteHistory.newBuilder().setSignature(signature)
                .setHistory(voteHistory).build();
        logger.info("Logging history {} (signature: {})", Representations.asString(voteHistory),
                Representations.asString(signature));
        return voteHistoryMap.put(voteHistory.getOwnerPublicKey().getHash(), signedVoteHistory);
    }

}
