package moe.cdn.cweb.dht;

import com.google.protobuf.Message;

/**
 * @author davix
 */
interface DhtNode<T extends Message> extends ManagedNode<T>, Shutdownable {
}
