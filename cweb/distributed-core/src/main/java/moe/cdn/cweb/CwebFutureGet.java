package moe.cdn.cweb;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * @author davix
 */
public class CwebFutureGet<T extends Message> extends FutureGetWrapper {
    private static final Logger logger = LogManager.getLogger();
    private final Parser<T> messageParser;

    public CwebFutureGet(FutureGet underlying, Parser<T> messageParser) {
        super(underlying);
        this.messageParser = messageParser;
    }

    public Collection<T> all() {
        Map<Number640, Data> dataMap = dataMap();
        Collection<T> result = new ArrayList<>(dataMap.size());
        for (Map.Entry<Number640, Data> entry : dataMap.entrySet()) {
            try {
                result.add(messageParser.parseFrom(entry.getValue().toBytes()));
            } catch (InvalidProtocolBufferException e) {
                logger.warn(new InvalidProtocolBufferMessage(e));
            }
        }
        return result;
    }

    public T one() {
        try {
            return messageParser.parseFrom(data().toBytes());
        } catch (InvalidProtocolBufferException e) {
            logger.warn(new InvalidProtocolBufferMessage(e));
            return null;
        }
    }
}
