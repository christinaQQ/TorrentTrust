package moe.cdn.cweb.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.util.concurrent.Futures;
import com.google.protobuf.Message;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMap;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;

public class CwebImportServiceImplTest {

    private static final KeyPair KEY_PAIR = KeyUtils.generateKeypair();

    private static final User USER_1 =
            User.newBuilder().setPublicKey(KEY_PAIR.getPublicKey()).setHandle("User 1").build();
    private static final Signature USER_1_SIGNATURE = SignatureUtils.signMessage(KEY_PAIR, USER_1);

    private static final Hash CONTENT_HASH = HashUtils.hashOf("Hello World");
    private static final Vote VOTE_1 = Vote.newBuilder().setOwnerPublicKey(KEY_PAIR.getPublicKey())
            .setContentHash(CONTENT_HASH).build();
    private static final Signature VOTE_1_SIGNATURE = SignatureUtils.signMessage(KEY_PAIR, VOTE_1);

    @Mock
    private CwebMap<Hash, SignedUserRecord> userMap;
    @Mock
    private CwebMap<Hash, SignedVote> voteMap;

    private CwebImportServiceImpl cwebImportServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cwebImportServiceImpl = new CwebImportServiceImpl(KEY_PAIR, userMap, voteMap);
    }

    @Test
    public void testImportUser() {
        when(userMap.put(USER_1.getPublicKey().getHash(), SignedUserRecord.newBuilder()
                .setSignature(USER_1_SIGNATURE).setUser(USER_1).build()))
                        .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importUser(USER_1));
    }

    @Test
    public void testImportVote() {
        when(voteMap.put(CONTENT_HASH,
                SignedVote.newBuilder().setSignature(VOTE_1_SIGNATURE).setVote(VOTE_1).build()))
                        .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importVote(VOTE_1));
    }

    @Test
    public void testImportSignatureUser() {
        when(userMap.put(USER_1.getPublicKey().getHash(), SignedUserRecord.newBuilder()
                .setSignature(USER_1_SIGNATURE).setUser(USER_1).build()))
                        .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importSignature(USER_1, USER_1_SIGNATURE));
    }

    @Test
    public void testImportSignatureVote() {
        when(voteMap.put(CONTENT_HASH,
                SignedVote.newBuilder().setSignature(VOTE_1_SIGNATURE).setVote(VOTE_1).build()))
                        .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importSignature(VOTE_1, VOTE_1_SIGNATURE));
    }

    @Test
    public void testImportSignatureNull() {
        assertFalse(cwebImportServiceImpl.importSignature((Message) null, USER_1_SIGNATURE));
    }
    
    @Test
    public void testImportSignatureFail() {
        when(userMap.put(USER_1.getPublicKey().getHash(), SignedUserRecord.newBuilder()
                .setSignature(USER_1_SIGNATURE).setUser(USER_1).build()))
                        .thenReturn(Futures.immediateFailedFuture(new RuntimeException()));
        assertFalse(cwebImportServiceImpl.importSignature(USER_1, USER_1_SIGNATURE));
    }

}
