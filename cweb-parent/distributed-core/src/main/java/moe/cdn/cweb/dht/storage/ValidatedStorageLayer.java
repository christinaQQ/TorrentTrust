package moe.cdn.cweb.dht.storage;

import java.security.PublicKey;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import net.tomp2p.dht.Storage;
import net.tomp2p.dht.StorageLayer;
import net.tomp2p.peers.Number320;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;
import net.tomp2p.utils.Pair;

/**
 * @author davix
 */
public class ValidatedStorageLayer extends StorageLayer {
    private final IncomingDataValidator incomingDataValidator;

    @Inject
    public ValidatedStorageLayer(Storage backend, IncomingDataValidator incomingDataValidator) {
        super(backend);
        this.incomingDataValidator = incomingDataValidator;
    }

    @Override
    public Enum<?> putConfirm(PublicKey publicKey, Number640 key, Data newData) {
        if (!incomingDataValidator.validate(key.domainKey(), newData)) {
            return PutStatus.FAILED_SECURITY;
        }
        return super.putConfirm(publicKey, key, newData);
    }

    @Override
    public Enum<?> updateMeta(PublicKey publicKey, Number640 key, Data newData) {
        if (!incomingDataValidator.validate(key.domainKey(), newData)) {
            return PutStatus.FAILED_SECURITY;
        }
        return super.updateMeta(publicKey, key, newData);
    }

    @Override
    public Enum<?> updateMeta(Number320 locationAndDomainKey, PublicKey publicKey,
                              PublicKey newPublicKey) {
        // pass through
        return super.updateMeta(locationAndDomainKey, publicKey, newPublicKey);
    }

    @Override
    public SortedMap<Number640, Byte> removeReturnStatus(Number640 from, Number640 to,
                                                         PublicKey publicKey) {
        // pass through
        return super.removeReturnStatus(from, to, publicKey);
    }

    @Override
    public NavigableMap<Number640, Data> removeReturnData(Number640 from, Number640 to,
                                                          PublicKey publicKey) {
        // pass through
        return super.removeReturnData(from, to, publicKey);
    }

    @Override
    public Pair<Data, Enum<?>> remove(Number640 key, PublicKey publicKey, boolean returnData) {
        // pass through
        return super.remove(key, publicKey, returnData);
    }

    @Override
    public Enum<?> put(Number640 key, Data newData, PublicKey publicKey, boolean putIfAbsent,
                       boolean domainProtection, boolean sendSelf) {
        if (!incomingDataValidator.validate(key.domainKey(), newData)) {
            return PutStatus.FAILED_SECURITY;
        }
        return super.put(key, newData, publicKey, putIfAbsent, domainProtection, sendSelf);
    }

    @Override
    public Map<Number640, Enum<?>> putAll(NavigableMap<Number640, Data> dataMap, PublicKey
            publicKey, boolean putIfAbsent, boolean domainProtection, boolean sendSelf) {
        Map<Number640, Enum<?>> failedValidation = new HashMap<>();
        NavigableMap<Number640, Data> passThrough = new TreeMap<>();
        for (Map.Entry<Number640, Data> kv : dataMap.entrySet()) {
            if (!incomingDataValidator.validate(kv.getKey().domainKey(), kv.getValue())) {
                failedValidation.put(kv.getKey(), PutStatus.FAILED_SECURITY);
            } else {
                passThrough.put(kv.getKey(), kv.getValue());
            }
        }
        Map<Number640, Enum<?>> superResults = super.putAll(passThrough, publicKey, putIfAbsent,
                domainProtection, sendSelf);
        return Stream.of(failedValidation, superResults) // merge maps
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
