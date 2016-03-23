package moe.cdn.cweb.dht;

import com.google.protobuf.Message;

/**
 * @author davix
 */
public interface DhtNode<T extends Message> extends ManagedNode<T>, Shutdownable {

}
