package moe.cdn.cweb.security;

import com.google.common.util.concurrent.Futures;
import com.google.protobuf.Message;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.TorrentTrustProtos;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.SignedVote;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.TorrentTrustProtos.Vote;
import moe.cdn.cweb.dht.CwebMultiMap;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CwebImportServiceImplTest {

    private static final KeyPair KEY_PAIR = KeyUtils.generateKeyPair();

    private static final User USER_1 =
            User.newBuilder().setPublicKey(KEY_PAIR.getPublicKey()).setHandle("User 1").build();
    private static final Signature USER_1_SIGNATURE =
            SignatureUtils.signMessageUnchecked(KEY_PAIR, USER_1);

    private static final Hash CONTENT_HASH = HashUtils.hashOf("Hello World");
    private static final Vote VOTE_1 = Vote.newBuilder().setOwnerPublicKey(KEY_PAIR.getPublicKey())
            .setContentHash(CONTENT_HASH).build();
    private static final Signature VOTE_1_SIGNATURE =
            SignatureUtils.signMessageUnchecked(KEY_PAIR, VOTE_1);

    @Mock
    private CwebMultiMap<SignedUser> userMap;
    @Mock
    private CwebMultiMap<SignedVote> voteMap;

    private CwebImportServiceImpl cwebImportServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cwebImportServiceImpl = new CwebImportServiceImpl(KEY_PAIR, userMap, voteMap);
    }

    @Test
    public void testImportUser() throws Exception {
        when(userMap.put(USER_1.getPublicKey().getHash(), SignedUser.newBuilder()
                .setSignature(USER_1_SIGNATURE).setUser(USER_1).build()))
                .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importUser(USER_1).get());
    }

    @Test
    public void testImportVote() throws Exception {
        when(voteMap.add(CONTENT_HASH,
                SignedVote.newBuilder().setSignature(VOTE_1_SIGNATURE).setVote(VOTE_1).build()))
                .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.addVote(VOTE_1).get());
    }

    @Test
    public void testImportSignatureUser() throws Exception {
        when(userMap.put(USER_1.getPublicKey().getHash(), TorrentTrustProtos.SignedUser.newBuilder()
                .setSignature(USER_1_SIGNATURE).setUser(USER_1).build()))
                .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.importSignature(USER_1, USER_1_SIGNATURE).get());
    }

    @Test
    public void testImportSignatureVote() throws Exception {
        when(voteMap.add(CONTENT_HASH,
                SignedVote.newBuilder().setSignature(VOTE_1_SIGNATURE).setVote(VOTE_1).build()))
                .thenReturn(Futures.immediateFuture(true));
        assertTrue(cwebImportServiceImpl.addSignature(VOTE_1, VOTE_1_SIGNATURE).get());
    }

    @Test
    public void testImportSignatureNull() {
        assertFalse(cwebImportServiceImpl.importSignature((Message) null, USER_1_SIGNATURE));
    }

    @Test
    public void testImportSignatureFail() throws Exception {
        when(userMap.put(
                USER_1.getPublicKey().getHash(),
                TorrentTrustProtos.SignedUser.newBuilder()
                        .setSignature(USER_1_SIGNATURE)
                        .setUser(USER_1).build()))
                .thenReturn(Futures.immediateFuture(false));
        assertFalse(cwebImportServiceImpl.importSignature(USER_1, USER_1_SIGNATURE).get());
    }

}
