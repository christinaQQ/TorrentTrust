package moe.cdn.cweb;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.google.common.collect.Iterables;
import com.google.protobuf.ByteString;

import moe.cdn.cweb.SecurityProtos.Hash;
import moe.cdn.cweb.SecurityProtos.Key;
import moe.cdn.cweb.SecurityProtos.Key.KeyType;
import moe.cdn.cweb.SecurityProtos.KeyPair;
import moe.cdn.cweb.dht.KeyEnvironment;
import moe.cdn.cweb.security.utils.HashUtils;
import moe.cdn.cweb.security.utils.KeyUtils;
import moe.cdn.cweb.security.utils.Representations;

public class IdentityEnvironment implements KeyEnvironment, Iterable<KeyPair> {

    private static final Logger logger = LogManager.getLogger();
    private static final String UNNAMED = "Unnamed";
    private static final Comparator<KeyPair> KEY_PAIR_COMPARATOR = (a, b) -> {
            BigInteger aValue =
                    new BigInteger(1, a.getPublicKey().getHash().getHashValue().toByteArray());
            BigInteger bValue =
                    new BigInteger(1, b.getPublicKey().getHash().getHashValue().toByteArray());
            return aValue.compareTo(bValue);
    };

    private final SortedMap<KeyPair, String> identities;
    private Optional<KeyPair> currentIdentity;

    private IdentityEnvironment() {
        identities = new TreeMap<>(KEY_PAIR_COMPARATOR);
        currentIdentity = Optional.empty();
    }

    /**
     * Construct an identity environment of only one identity
     * 
     * @param keyPair
     * @param handle
     */
    public IdentityEnvironment(KeyPair keyPair, String handle) {
        this();
        identities.put(keyPair, handle);
        currentIdentity = Optional.of(keyPair);
    }

    private static KeyPair readKeyPair(Section keyPairSection) {
        byte[] decodedKey = Base64.getDecoder().decode(keyPairSection.get("privateKey"));
        Key privateKey = Key.newBuilder()
                .setType(KeyType.PRIVATE)
                .setRaw(ByteString.copyFrom(decodedKey))
                        .setHash(HashUtils.hashOf(decodedKey)).build();
        String publicKeyBase64 = keyPairSection.get("publicKey");
        Key publicKey;
        if (publicKeyBase64 == null) {
            // no public key; generate the public key from the private key
            publicKey = KeyUtils.fromKey(KeyUtils.toPublicKey(
                    KeyUtils.importPrivateKey(privateKey)));
        } else {
            ByteString publicKeyRaw = ByteString.copyFrom(
                    Base64.getDecoder().decode(publicKeyBase64.getBytes()));
            publicKey = Key.newBuilder().setType(KeyType.PUBLIC)
                    .setRaw(publicKeyRaw)
                    .setHash(HashUtils.hashOf(publicKeyRaw))
                    .build();
        }
        logger.info("Loaded key pair with public portion {}", Representations.asString(publicKey));
        return KeyPair.newBuilder().setPrivateKey(privateKey).setPublicKey(publicKey).build();
    }

    public static IdentityEnvironment readFromFile(File configFile) throws IOException {
        IdentityEnvironment identityEnvironment = new IdentityEnvironment();

        Ini iniFile = new Ini();
        iniFile.getConfig().setMultiSection(true);
        iniFile.load(configFile);
        if (iniFile.containsKey("identity")) {
            for (Section identity : iniFile.getAll("identity")) {
                if (!identity.containsKey("privateKey")) {
                    logger.warn("No `privateKey' key in [identity] section");
                    continue;
                }
                KeyPair keyPair = readKeyPair(identity);
                if (identity.containsKey("handle")) {
                    identityEnvironment.addIdentity(keyPair, identity.get("handle"));
                } else {
                    identityEnvironment.addIdentity(keyPair, UNNAMED);
                }
            }
        }

        // Assign a default
        if (iniFile.containsKey("default")) {
            Section defaultSection = iniFile.get("default");
            if (defaultSection.containsKey("privateKey")) {
                KeyPair defaultKey = readKeyPair(defaultSection);
                if (!identityEnvironment.identities.containsKey(defaultKey)) {
                    identityEnvironment.addIdentity(defaultKey, defaultSection.containsKey("handle")
                                                                ? defaultSection.get("handle") :
                                                                "default");
                }
                identityEnvironment.currentIdentity = Optional.of(defaultKey);
            } else if (defaultSection.containsKey("handle")) {
                // Try to find the key
                for (Entry<KeyPair, String> entry : identityEnvironment.identities.entrySet()) {
                    if (entry.getValue().equals(defaultSection.get("handle"))) {
                        logger.info("Loaded key for identity {} [{}]", defaultSection.get("handle"),
                                Representations.asString(entry.getKey().getPublicKey()));
                        identityEnvironment.currentIdentity = Optional.of(entry.getKey());
                        break;
                    }
                }
            }
        }
        if (!identityEnvironment.currentIdentity.isPresent()) {
            logger.warn("No default identity specified. ");
            if (!identityEnvironment.identities.isEmpty()) {
                identityEnvironment.currentIdentity = Optional.ofNullable(
                        Iterables.getFirst(identityEnvironment.identities.keySet(), null));
                logger.warn("Picking {} as default identity.", Representations
                        .asString(identityEnvironment.currentIdentity.get().getPublicKey()));
            } else {
                logger.warn("Initializing empty key environment!");
            }
        }

        return identityEnvironment;
    }

    public static void writeToFile(File configFile, IdentityEnvironment identityEnvironment)
            throws IOException {
        if (!configFile.exists() || !configFile.isFile() || !configFile.canWrite()) {
            throw new RuntimeException("Cannot write to location");
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyPair getKeyPair() {
        return currentIdentity.get();
    }

    /**
     * Gets the handle that was configured in the config file. This should be
     * the same as the one in the DHT but is not necessarily the case.
     *
     * @return the handle or null if the identity was not part of the
     * configuration
     */
    public String getConfiguredHandle(KeyPair identity) {
        return identities.get(identity);
    }

    /**
     * Adds an identity to the environment. Note that just doing add does not
     * save this identity.
     *
     * @param keyPair key pair
     * @param handle  handle used
     */
    public void addIdentity(KeyPair keyPair, String handle) {
        identities.put(keyPair, handle);
        logger.info("Added identity with handle {} and public key {}", handle,
                Representations.asString(keyPair.getPublicKey()));
    }

    public boolean switchIdentity(KeyPair keyPair) {
        if (identities.containsKey(keyPair)) {
            currentIdentity = Optional.of(keyPair);
            return true;
        }
        if (keyPair != null && keyPair.hasPrivateKey() && keyPair.hasPublicKey()) {
            identities.put(keyPair, UNNAMED);
            currentIdentity = Optional.of(keyPair);
            logger.warn("Requested to switch to an identity with a previously unseen key pair. "
                            + "Added key pair with public key {}",
                    Representations.asString(keyPair.getPublicKey()));
            return true;
        }
        if (keyPair != null) {
            logger.error("Cannot switch to invalid key pair {}", Representations.asString(keyPair
                    .getPublicKey()));
        }
        return false;
    }

    public boolean switchIdentity(Hash publicKeyHash) {
        for (KeyPair keyPair : identities.keySet()) {
            if (keyPair.getPublicKey().getHash().equals(publicKeyHash)) {
                logger.info("Switching to identity with public key hash {}",
                        Representations.asString(publicKeyHash));
                currentIdentity = Optional.of(keyPair);
                return true;
            }
        }
        logger.error("No known identity with public key hash {}", publicKeyHash);
        return false;
    }

    @Override
    public Iterator<KeyPair> iterator() {
        return identities.keySet().iterator();
    }
}
