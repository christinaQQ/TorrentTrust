package moe.cdn.cweb.dht.internal.tomp2pcompat;

import java.util.Map;

import net.tomp2p.dht.FuturePut;
import net.tomp2p.futures.FutureDone;
import net.tomp2p.peers.Number640;
import net.tomp2p.peers.PeerAddress;

/**
 * API exposed by tomp2p for a "put" or "add" operation.
 */
public interface PutResponse {
    /**
     * Finish the future and set the keys that have been stored. Success or
     * failure is determined if the communication was successful. This means
     * that we need to further check if the other peers have denied the storage
     * (e.g., due to no storage space, no security permissions). Further
     * evaluation can be retrieved with {@link #avgStoredKeys()} or if the
     * evaluation should be done by the user, use {@link #rawKeys()}.
     *
     * @param rawResult
     * @param futuresCompleted
     */
    void storedKeys(Map<PeerAddress, Map<Number640, Byte>> rawResult,
                    FutureDone<Void> futuresCompleted);

    /**
     * @return The average keys received from the DHT. Only evaluates rawKeys.
     */
    double avgStoredKeys();

    /**
     * Returns the raw result from the storage or removal operation.
     *
     * @return The raw keys and the information which peer has been contacted
     */
    Map<PeerAddress, Map<Number640, Byte>> rawResult();

    /**
     * Checks if the minimum of expected results have been reached. This flag is
     * also used for determining the success or failure of this future for put
     * and send_direct.
     *
     * @return True, if expected minimum results have been reached.
     */
    boolean isMinReached();

    /**
     * Returns the keys that have been stored or removed after evaluation. The
     * evaluation gets rid of the PeerAddress information, by either a majority
     * vote or cumulation. Use {@link FuturePut#evalKeys()} instead of this
     * method.
     *
     * @return The keys that have been stored or removed
     */
    Map<Number640, Integer> result();

    boolean isSuccess();

    boolean isSuccessPartially();
}
