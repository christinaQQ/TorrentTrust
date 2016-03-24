package moe.cdn.cweb.dht;

import com.google.protobuf.Message;

/**
 * @author davix
 */
interface ManagedDhtNode<T extends Message> extends DhtNode<T>, Shutdownable {
}
