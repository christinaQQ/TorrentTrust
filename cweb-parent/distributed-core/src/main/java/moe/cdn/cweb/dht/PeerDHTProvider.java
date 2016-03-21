package moe.cdn.cweb.dht;

import com.google.inject.throwingproviders.CheckedProvider;
import net.tomp2p.dht.PeerDHT;

import java.io.IOException;

/**
 * @author davix
 */
public interface PeerDhtProvider extends CheckedProvider<PeerDHT> {
    @Override
    PeerDHT get() throws IOException;
}
