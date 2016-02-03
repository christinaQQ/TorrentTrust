package moe.cdn.cweb.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;

public class FakeSignatureValidationServiceImplTest {

    private static final KeyPair KEY_PAIR_ALICE = generateKeyPair();
    private static final KeyPair KEY_PAIR_BOB = generateKeyPair();
    private static final KeyPair KEY_PAIR_MALLORY = generateKeyPair();

    private static final User ALICE = generateUser(KEY_PAIR_ALICE.getPublicKey(), "Alice");
    private static final User BOB = generateUser(KEY_PAIR_BOB.getPublicKey(), "Bob");

    private static final List<SignedUserRecord> WEB_OF_TRUST;

    private static final byte[] MESSAGE = "Attack at dawn".getBytes();

    private FakeSignatureValidationServiceImpl validator;

    static {
        WEB_OF_TRUST = new LinkedList<>();
        WEB_OF_TRUST.add(signUserRecord(KEY_PAIR_ALICE, ALICE));
        WEB_OF_TRUST.add(signUserRecord(KEY_PAIR_BOB, BOB));
    }

    // We should actually mock things at this point
    public FakeSignatureValidationServiceImplTest() {
        KeyLookupService keyLookupService = new FakeKeyLookupServiceImpl(WEB_OF_TRUST);
        validator = new FakeSignatureValidationServiceImpl(keyLookupService);
    }

    @Test
    public void testCanValidateAliceSignature() {
        // Create a signed message
        Signature legitSignature = sign(MESSAGE, KEY_PAIR_ALICE);
        // Validate it
        assertTrue(validator.validate(MESSAGE, legitSignature));
    }

    @Test
    public void testCannotValidateIncorrectSignature() {
        // Create a signed message signing from jim
        Signature legitSignature = sign(MESSAGE, KEY_PAIR_ALICE);
        Signature notSoLegitSignature =
                legitSignature.toBuilder().setPublicKey(KEY_PAIR_BOB.getPublicKey()).build();
        // Validate it
        assertFalse(validator.validate(MESSAGE, notSoLegitSignature));;
    }

    @Test
    public void testCannotValidateMallorySignature() {
        Signature nonExistantUserSignature = sign(MESSAGE, KEY_PAIR_MALLORY);
        assertFalse(validator.validate(MESSAGE, nonExistantUserSignature));
    }

    private static Signature sign(byte[] data, KeyPair keypair) {
        try {
            java.security.Signature signer = java.security.Signature.getInstance("SHA256withRSA");
            PKCS8EncodedKeySpec privateKeySpec =
                    new PKCS8EncodedKeySpec(keypair.getPrivateKey().getRaw().toByteArray());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            signer.initSign(kf.generatePrivate(privateKeySpec));
            signer.update(data);
            return Signature.newBuilder().setPublicKey(keypair.getPublicKey())
                    .setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                    .setSignature(ByteString.copyFrom(signer.sign()))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private static SignedUserRecord signUserRecord(KeyPair keypair, User user) {
        return SignedUserRecord.newBuilder().setUser(user)
                .setSignature(sign(user.toByteArray(), keypair)).build();
    }

    private static User generateUser(Key publicKey, String handle) {
        return User.newBuilder().setHandle(handle).setPublicKey(publicKey).build();
    }

    private static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
            java.security.KeyPair keypair = keyGen.generateKeyPair();
            return KeyPair.newBuilder()
                    .setPublicKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPublic().getEncoded()))
                            .setHash(hashOf(keypair.getPublic().getEncoded())).build())
                    .setPrivateKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPrivate().getEncoded()))
                            .setHash(hashOf(keypair.getPrivate().getEncoded())).build())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Some secure algorithm was missing.", e);
        }
    }

    private static Hash hashOf(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hash.newBuilder().setAlgorithm(HashAlgorithm.SHA256)
                    .setHashvalue(ByteString.copyFrom(md.digest(bytes))).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }

}
