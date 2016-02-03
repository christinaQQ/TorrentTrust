package moe.cdn.cweb;

import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Hash.HashAlgorithm;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.SecurityProtos.Signature;
import moe.cdn.cweb.SecurityProtos.Signature.SignatureAlgorithm;
import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.TorrentTrustProtos.User;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.Futures;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.replication.IndirectReplication;

public class WorkingExampleHashMap17 {

    static final Random RND = new Random(17);

    static PeerDHT[] createAndAttachPeersDHT(int nr, int port) throws IOException {
        // TODO replace
        // this is an array where [0] is the master
        PeerDHT[] peers = new PeerDHT[nr];
        for (int i = 0; i < nr; i++) {
            if (i == 0) {
                peers[0] = new PeerBuilderDHT(
                        new PeerBuilder(new Number160(RND)).ports(port).start())
                        .start();
            } else {
                peers[i] = new PeerBuilderDHT(
                        new PeerBuilder(new Number160(RND)).masterPeer(peers[0].peer()).start())
                        .start();
            }
            new IndirectReplication(peers[i]).replicationFactor(5).start();
        }
        return peers;
    }

    /**
     * Bootstraps peers to the first peer in the array.
     *
     * @param peers The peers that should be bootstrapped
     */
    static void bootstrap(PeerDHT[] peers) {
        // tell all peers about each other starting from master at 0
        // TODO replace
        Collection<PeerAddress> all = Arrays.stream(peers).map(PeerDHT::peerAddress)
                .collect(Collectors.toList());
        List<FutureBootstrap> allFutures = Arrays.stream(peers)
                .map(p -> p.peer().bootstrap().bootstrapTo(all).start())
                .collect(Collectors.toList());
        Futures.whenAll(allFutures).awaitUninterruptibly();
    }

    public static void main(String[] args) throws Exception {
        // TODO: http://lists.tomp2p.net/pipermail/users/2013-July/000266.html
        PeerDHT master = null;
        try {
            PeerDHT[] peers = createAndAttachPeersDHT(100, 4001);
            master = peers[0];

            bootstrap(peers);

            CwebCollection<SignedUserRecord> sender1 = new CwebCollection<>(peers[0],
                    Number160.createHash("location"), Number160.createHash("domain"),
                    SignedUserRecord.PARSER);
            CwebCollection<SignedUserRecord> sender2 = new CwebCollection<>(peers[17],
                    Number160.createHash("location"), Number160.createHash("domain"),
                    SignedUserRecord.PARSER);

            CwebCollection<SignedUserRecord> receiver = new CwebCollection<>(peers[23],
                    Number160.createHash("location"), Number160.createHash("domain"),
                    SignedUserRecord.PARSER);
            
            // Generate two keypairs
            
            sender1.add(buildSignedUserRecord(generateKeypair(), "17"))
                    .awaitUninterruptibly();
            sender1.add(buildSignedUserRecord(generateKeypair(), "18"))
                    .awaitUninterruptibly();
            sender2.add(buildSignedUserRecord(generateKeypair(), "19"))
                    .awaitUninterruptibly();

            CwebFutureGet<SignedUserRecord> futureGet = receiver.doGet();
            futureGet.get().all().forEach(d -> System.out.println("received: " + d));
        } finally {
            if (master != null) {
                master.shutdown();
            }
        }
    }

    private static Hash hashOf(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Hash.newBuilder()
                    .setAlgorithm(HashAlgorithm.SHA256)
                    .setHashvalue(ByteString.copyFrom(md.digest(bytes)))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm guaranteed to exist did not.", e);
        }
    }
    
    private static KeyPair generateKeypair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, SecureRandom.getInstance("SHA1PRNG"));
            java.security.KeyPair keypair = keyGen.generateKeyPair();
            return KeyPair.newBuilder()
                    .setPublicKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPublic().getEncoded()))
                            .setHash(hashOf(keypair.getPublic().getEncoded()))
                            .build())
                    .setPrivateKey(Key.newBuilder()
                            .setRaw(ByteString.copyFrom(keypair.getPrivate().getEncoded()))
                            .setHash(hashOf(keypair.getPrivate().getEncoded()))
                            .build())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Some secure algorithm was missing.", e);
        }
    }
    
    private static Signature signMessage(KeyPair keypair, byte[] message) {
        return Signature.newBuilder()
                .setAlgorithm(SignatureAlgorithm.SHA256withRSA)
                .setPublicKey(keypair.getPublicKey())
                .setSignature(ByteString.copyFrom(new byte[]{}))
                .build();
    }
    
    private static SignedUserRecord buildSignedUserRecord(KeyPair keypair, String handle) {
        User userRecord = User.newBuilder()
                .setPublicKey(keypair.getPublicKey())
                .setHandle(handle)
                .build();
        return SignedUserRecord.newBuilder()
                    .setUser(userRecord)
                    .setSignature(signMessage(keypair, userRecord.toByteArray()))
                    .build();
    }
}