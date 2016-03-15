package moe.cdn.cweb.security;

import java.util.concurrent.ExecutionException;

import com.google.inject.Inject;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.dht.annotations.UserMap;
import moe.cdn.cweb.dht.annotations.VoteMap;
import moe.cdn.cweb.security.utils.SignatureUtils;

public class CwebImportServiceImpl implements CwebImportService {

    private final KeyPair userKeyPair;
    private final CwebMap<Hash, SignedUserRecord> userMap;
    private final CwebMap<Hash, SignedVote> voteMap;

    @Inject
    public CwebImportServiceImpl(KeyPair userKeyPair,
            @UserMap CwebMap<Hash, SignedUserRecord> userMap,
            @VoteMap CwebMap<Hash, SignedVote> voteMap) {
        this.userKeyPair = userKeyPair;
        this.userMap = userMap;
        this.voteMap = voteMap;
    }

    @Override
    public Signature sign(byte[] data, KeyPair keypair) {
        return SignatureUtils.signMessage(keypair, data);
    }

    @Override
    public boolean importSignature(User user, Signature signature) {
        SignedUserRecord signedUserRecord =
                SignedUserRecord.newBuilder().setSignature(signature).setUser(user).build();
        try {
            return userMap.put(signedUserRecord.getUser().getPublicKey().getHash(), signedUserRecord)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public boolean importSignature(Vote vote, Signature signature) {
        SignedVote signedVote = SignedVote.newBuilder().setSignature(signature).setVote(vote).build();
        try {
            return voteMap.put(signedVote.getVote().getContentHash(), signedVote).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public boolean importVote(Vote vote) {
        return importSignature(vote, SignatureUtils.signMessage(userKeyPair, vote.toByteArray()));
    }

    @Override
    public boolean importUser(User user) {
        return importSignature(user, SignatureUtils.signMessage(userKeyPair, user.toByteArray()));
    }

}
