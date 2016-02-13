package moe.cdn.cweb.dht;

import net.tomp2p.futures.FutureDone;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;

import java.util.Map;

/**
 * @author davix
 */
interface ResponseData {
    Map<PeerAddress, Map<Number640, Data>> rawData();

    Map<PeerAddress, DigestResult> rawDigest();

    Map<Number640, Data> dataMap();

    Data data();

    DigestResult digest();

    Map<PeerAddress, Byte> rawStatus();

    void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData, Map<PeerAddress,
            DigestResult> rawDigest, Map<PeerAddress, Byte> rawStatus, FutureDone<Void>
                              futuresCompleted);

    boolean isMinReached();

    boolean isEmpty();
}
