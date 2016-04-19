package moe.cdn.cweb.security.utils;

import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.*;

import java.util.Base64;

/**
 * Methods to turn some of the protos to prettier formats
 *
 * @author jim
 */
public final class Representations {
    /**
     * This class should not be instantiated.
     */
    private Representations() {
    }

    public static String asString(Hash hash) {
        return String.format("Hash<%s>[%s]", hash.getAlgorithm(),
                asString(hash.getHashValue().toByteArray()));
    }

    public static String asString(Key key) {
        return String.format("Key<%s>(%s, raw='%s')", key.getType(), asString(key.getHash()),
                asString(key.getRaw().toByteArray()));
    }

    public static String asString(Signature signature) {
        return String.format("Signature<%s>(key=%s, raw='%s')", signature.getAlgorithm(),
                asString(signature.getPublicKey()),
                asString(signature.getSignature().toByteArray()));
    }

    public static String asString(Signature signature, Message message) {
        return String.format("Signature<%s>(key=%s, %s)", signature.getAlgorithm(),
                asString(signature.getPublicKey()),
                SignatureUtils.validateMessage(signature, message));
    }

    public static String asString(User user) {
        return String.format("User(handle='%s', key=%s, trusted=[:%d])", user.getHandle(),
                asString(user.getPublicKey()), user.getTrustedCount());
    }

    public static String asString(Vote vote) {
        return String.format("Vote(hash=%s, owner=%s, assertions=[:%d])",
                asString(vote.getContentHash()), asString(vote.getOwnerPublicKey()),
                vote.getAssertionCount());
    }

    public static String asString(VoteHistory voteHistory) {
        return String.format("VoteHistory(owner=%s, records=[:%d])",
                asString(voteHistory.getOwnerPublicKey()), voteHistory.getContentHashCount());
    }

    public static String asString(SignedUser signedUser) {
        return String.format("SignedUser(user=%s, signature=%s)", asString(signedUser.getUser()),
                asString(signedUser.getSignature(), signedUser.getUser()));
    }

    public static String asString(SignedVote signedVote) {
        return String.format("SignedVote(user=%s, signature=%s)", asString(signedVote.getVote()),
                asString(signedVote.getSignature(), signedVote.getVote()));
    }

    public static String asString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

}
