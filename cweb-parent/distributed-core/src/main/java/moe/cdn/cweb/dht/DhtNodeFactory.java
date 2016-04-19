package moe.cdn.cweb.dht;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import moe.cdn.cweb.dht.internal.ManagedPeerDhtPeer;

public interface DhtNodeFactory {
    <T extends Message> ManagedDhtNode<T> create(ManagedPeerDhtPeer self,
                                                 String domainKey,
                                                 Parser<T> messageParser);
}
