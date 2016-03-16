package moe.cdn.cweb.dht;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import net.tomp2p.dht.PeerDHT;

public interface DhtNodeFactory {
    <T extends Message> DhtNode<T> create(PeerDHT self, String domainKey, Parser<T> messageParser);
}
