package moe.cdn.cweb;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import moe.cdn.cweb.TorrentTrustProtos.SignedUser;
import moe.cdn.cweb.dht.DhtNode;
import moe.cdn.cweb.security.CwebImportService;
import moe.cdn.cweb.security.KeyLookupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Entry point for testing. Do not use in production.
 */
public class CwebExecutable {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Usage: java moe.cdn.cweb.CwebExecutable [[id host_and_port] ...]
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Injector injector = Guice.createInjector(new CwebModule(args));
        CwebApi cwebApi = injector.getInstance(CwebApi.class);
        DhtNode<TorrentTrustProtos.SignedVote> signedVoteDhtNode = injector.getInstance(
                new Key<DhtNode<TorrentTrustProtos.SignedVote>>() {
                });
        CwebImportService cwebImportService = injector.getInstance(CwebImportService.class);

        TorrentTrustProtos.User user = TorrentTrustProtos.User.newBuilder()
                .setHandle("name")
                .setPublicKey(injector.getInstance(SecurityProtos.KeyPair.class).getPublicKey())
                .build();
        boolean b = cwebImportService.importUser(user);
        System.out.println("imported user <user>: " + b);

        KeyLookupService keyLookupService = injector.getInstance(KeyLookupService.class);
        ListenableFuture<Optional<SignedUser>> owner = keyLookupService.findOwner(user
                .getPublicKey());
        System.out.println("owner of <user>'s public key: " + owner.get());

        signedVoteDhtNode.shutdown().get();
    }

}
