package moe.cdn.cweb.dht;

import com.google.protobuf.Message;

/**
 * DHT node that can be managed
 * 
 * @author davix, jim
 */
interface ManagedDhtNode<T extends Message> extends DhtNode<T>, Shutdownable {
}
