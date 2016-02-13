package moe.cdn.cweb.vote;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.TorrentTrustProtos.Vote;

public class VoteUtils {
    private VoteUtils() {}

    public static Vote.Builder createVote(String contentHash, Key publicKey) {
        return Vote.newBuilder()
                .setContentHash(Hash.newBuilder().setAlgorithm(HashAlgorithm.TORRENT)
                        .setHashvalue(ByteString.copyFromUtf8(contentHash)))
                .setOwnerPublicKey(publicKey);
    }
}
