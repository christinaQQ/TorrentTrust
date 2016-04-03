package moe.cdn.cweb.security;

import com.google.inject.Inject;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import moe.cdn.cweb.security.utils.Representations;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

public class CwebImportServiceImpl implements CwebImportService {
    private static final Logger logger = LogManager.getLogger();

    private final KeyPair userKeyPair;
    private final CwebMultiMap<SignedUser> userMap;
    private final CwebMultiMap<SignedVote> voteMap;

    @Inject
    public CwebImportServiceImpl(KeyPair userKeyPair,
                                 @UserDomain CwebMultiMap<SignedUser> userMap,
                                 @VoteDomain CwebMultiMap<SignedVote> voteMap) {

        this.userKeyPair = checkNotNull(userKeyPair);
        this.userMap = checkNotNull(userMap);
        this.voteMap = checkNotNull(voteMap);
    }

    @Override
    public Signature sign(KeyPair keyPair, byte[] data)
            throws SignatureException, InvalidKeyException {
        return SignatureUtils.signMessage(keyPair, data);
    }

    @Override
    public Future<Boolean> addVote(Vote vote) throws SignatureException, InvalidKeyException {
        return addSignature(vote, sign(userKeyPair, vote.toByteArray()));
    }

    @Override
    public Future<Boolean> addSignature(Vote vote, Signature signature) {
        SignedVote signedVote = SignedVote.newBuilder()
                .setSignature(signature)
                .setVote(vote).build();
        logger.info("Importing vote {} (signature: {})", Representations.asString(vote),
                Representations.asString(signature));
        return voteMap.add(signedVote.getVote().getContentHash(), signedVote);
    }

    @Override
    public Future<Boolean> importUser(User user) throws SignatureException, InvalidKeyException {
        return importSignature(user, sign(userKeyPair, user.toByteArray()));
    }

    @Override
    public Future<Boolean> importSignature(User user, Signature signature) {
        SignedUser signedUser = SignedUser.newBuilder()
                .setSignature(signature)
                .setUser(user).build();
        logger.info("Importing user {} (signature: {})", Representations.asString(user),
                Representations.asString(signature));
        return userMap.put(signedUser.getUser().getPublicKey().getHash(), signedUser);
    }

}
