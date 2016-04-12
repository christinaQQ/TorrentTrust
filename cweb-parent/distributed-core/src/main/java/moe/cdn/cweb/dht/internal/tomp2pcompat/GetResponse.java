package moe.cdn.cweb.dht.internal.tomp2pcompat;

import java.util.Map;

import net.tomp2p.futures.FutureDone;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.DigestResult;
import net.tomp2p.storage.Data;

/**
 * API exposed by tomp2p for a "get" operation.
 *
 * @author davix
 */
public interface GetResponse {
    /**
     * Finish the future and set the keys and data that have been received.
     *
     * @param rawData The keys and data that have been received with information
     *        from which peer it has been received.
     * @param rawDigest The hashes of the content stored with information from
     *        which peer it has been received.
     * @param rawStatus
     * @param futuresCompleted
     */
    void receivedData(Map<PeerAddress, Map<Number640, Data>> rawData,
            Map<PeerAddress, DigestResult> rawDigest,
            Map<PeerAddress, Byte> rawStatus,
            FutureDone<Void> futuresCompleted);

    /**
     * Returns the raw data from the get operation.
     *
     * @return The raw data and the information which peer has been contacted
     */
    Map<PeerAddress, Map<Number640, Data>> rawData();

    /**
     * @return The raw digest information with hashes of the content and the
     *         information which peer has been contacted
     */
    Map<PeerAddress, DigestResult> rawDigest();

    /**
     * @return The raw digest information with hashes of the content and the
     *         information which peer has been contacted
     */
    Map<PeerAddress, Byte> rawStatus();

    /**
     * Return the digest information from the get() after evaluation. The
     * evaluation gets rid of the PeerAddress information, by either a majority
     * vote or cumulation.
     *
     * @return The evaluated digest information that have been received.
     */
    DigestResult digest();

    /**
     * Return the data from get() after evaluation. The evaluation gets rid of
     * the PeerAddress information, by either a majority vote or cumulation.
     *
     * @return The evaluated data that have been received.
     */
    Map<Number640, Data> dataMap();

    /**
     * @return The first data object from get() after evaluation.
     */
    Data data();

    /**
     * Checks if the minimum of expected results have been reached. This flag is
     * also used for determining the success or failure of this future for put
     * and send_direct.
     *
     * @return True, if expected minimum results have been reached.
     */
    boolean isMinReached();

    boolean isEmpty();
}
