package moe.cdn.cweb;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/**
 * @author davix
 */
public class InvalidProtocolBufferMessage implements Message {
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
