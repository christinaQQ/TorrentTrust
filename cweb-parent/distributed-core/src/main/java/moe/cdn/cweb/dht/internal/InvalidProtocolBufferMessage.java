package moe.cdn.cweb.dht.internal;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.logging.log4j.message.Message;

/**
 * @author davix
 */
class InvalidProtocolBufferMessage implements Message {

    private static final long serialVersionUID = -6369395150347435770L;

    private final InvalidProtocolBufferException e;

    public InvalidProtocolBufferMessage(InvalidProtocolBufferException e) {
        this.e = e;
    }

    @Override
    public String getFormattedMessage() {
        return e.getLocalizedMessage() + ": " + e.getUnfinishedMessage();
    }

    @Override
    public String getFormat() {
        return "";
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return e;
    }

}
