package moe.cdn.cweb.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Optional;

import moe.cdn.cweb.dht.security.KeyLookupService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.util.concurrent.Futures;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.TorrentTrustProtos.User;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.SignatureUtils;

public class SignatureValidationServiceImplTest {
    private static final KeyPair KEY_PAIR = KeyUtils.generateKeyPair();
    private static final KeyPair KEY_PAIR_ALT = KeyUtils.generateKeyPair();
    private static final User USER =
            User.newBuilder().setPublicKey(KEY_PAIR.getPublicKey()).setHandle("User A").build();
    private static final Hash SAMPLE_MESSAGE = HashUtils.hashOf("Hello World");

    @Mock
    private KeyLookupService keyLookupService;

    private SignatureValidationServiceImpl signatureValidationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        signatureValidationService = new SignatureValidationServiceImpl(keyLookupService);
    }

    @Test
    public void testSignatureValidationServiceImpl() {

    }

    @Test
    public void testValidateSelfSignedSignatureUserMessageValid() {
        assertTrue(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE), USER, SAMPLE_MESSAGE));
    }

    @Test
    public void testValidateSelfSignedSignatureUserMessageInvalid() {
        assertFalse(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR_ALT, SAMPLE_MESSAGE), USER, SAMPLE_MESSAGE));
    }

    @Test
    public void testValidateSelfSignedSignatureUserByteArray() {
        assertTrue(signatureValidationService.validateSelfSigned(
                SignatureUtils.signMessage(KEY_PAIR, SAMPLE_MESSAGE), USER,
                SAMPLE_MESSAGE.toByteArray()));
    }

    @Test
    public void testValidateAndCheckSignatureKeyInNetworkSignatureByteArray() {
        when(keyLookupService.findOwner(KEY_PAIR.getPublicKey()))
                .thenReturn(Futures.immediateFuture(Optional.empty()));
    }

    @Test
    public void testValidateAndCheckSignatureKeyInNetworkSignatureMessage() {
        fail("Not yet implemented");
    }

}
