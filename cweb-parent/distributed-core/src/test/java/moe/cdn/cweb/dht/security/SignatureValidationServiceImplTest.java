package moe.cdn.cweb.dht.security;

import com.google.common.util.concurrent.Futures;
import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class SignatureValidationServiceImplTest {
    private static final KeyPair KEY_PAIR = KeyUtils.generateKeyPair();
    private static final KeyPair KEY_PAIR_ALT = KeyUtils.generateKeyPair();
    private static final User USER =
            User.newBuilder().setPublicKey(KEY_PAIR.getPublicKey()).setHandle("User A").build();
    private static final SignedUser SIGNED_USER = SignedUser.newBuilder().setUser(USER)
            .setSignature(SignatureUtils.signMessageUnchecked(KEY_PAIR, USER)).build();
    private static final Hash SAMPLE_MESSAGE = HashUtils.hashOf("Hello World");

    @Mock
    private KeyLookupService userKeyService;

    private SignatureValidationServiceImpl signatureValidationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureValidationService = new SignatureValidationServiceImpl(userKeyService);
    }

    @Test
    public void testValidateSelfSignedSignatureUserMessageValid() throws Exception {
        assertTrue(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE), USER, SAMPLE_MESSAGE));
    }

    @Test
    public void testValidateSelfSignedSignatureUserMessageInvalid() throws Exception {
        assertFalse(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR_ALT, SAMPLE_MESSAGE), USER, SAMPLE_MESSAGE));
    }

    @Test
    public void testValidateSelfSignedSignatureUserByteArray() throws Exception {
        assertTrue(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE), USER,
                SAMPLE_MESSAGE.toByteArray()));
    }

    @Test
    public void testValidateAndCheckSignatureKeyInNetworkSignatureByteArray() throws Exception {
        when(userKeyService.findOwner(KEY_PAIR.getPublicKey()))
                .thenReturn(Futures.immediateFuture(Optional.of(SIGNED_USER)));
        assertTrue(signatureValidationService.validateAndCheckSignatureKeyInNetwork(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE), SAMPLE_MESSAGE));
    }

    @Test
    public void testValidateAndCheckSignatureKeyInNetworkSignatureMessage() throws Exception {
        when(userKeyService.findOwner(KEY_PAIR.getPublicKey()))
                .thenReturn(Futures.immediateFuture(Optional.of(SIGNED_USER)));
        assertTrue(signatureValidationService.validateAndCheckSignatureKeyInNetwork(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE),
                SAMPLE_MESSAGE.toByteArray()));
    }
}
