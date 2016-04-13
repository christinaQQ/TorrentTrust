package moe.cdn.cweb.dht.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import moe.cdn.cweb.dht.internal.tomp2pcompat.GetResponse;
import moe.cdn.cweb.dht.internal.tomp2pcompat.GetResponseWrapper;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

/**
 * @author davix
 */
public class CwebGetResultsImpl<T extends Message> extends GetResponseWrapper
        implements CwebGetResults<T> {
    Logger logger = LogManager.getLogger();

    private Parser<T> messageParser;

    CwebGetResultsImpl(Parser<T> messageParser, GetResponse getResponse) {
        super(getResponse);
        this.messageParser = messageParser;
    }

    @Override
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

    @Override
    public T one() {
        Data data = data();
        if (data == null) {
            return null;
        }
        try {
            return messageParser.parseFrom(data.toBytes());
        } catch (InvalidProtocolBufferException e) {
            logger.warn(new InvalidProtocolBufferMessage(e));
            return null;
        }
    }
}
