package moe.cdn.cweb.dht;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import moe.cdn.cweb.dht.CwebFutureGet;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

/**
 * @author davix
 */
class CwebFutureGetImpl<T extends Message> extends FutureGetWrapper implements CwebFutureGet<T> {
    private static final Logger logger = LogManager.getLogger();
    private final Parser<T> messageParser;

    public CwebFutureGetImpl(FutureGet underlying, Parser<T> messageParser) {
        super(underlying);
        this.messageParser = messageParser;
    }

    @Override
    public CwebData get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        return new CwebData(super.get(timeout, unit));
    }

    @Override
    public CwebData get() throws InterruptedException {
        return new CwebData(super.get());
    }

    @Override
    public Collection<T> getAll(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {
        return get(timeout, unit).all();
    }

    @Override
    public Collection<T> getAll() throws InterruptedException {
        return get().all();
    }

    /**
     * @author davix
     */
    public class CwebData extends ResponseDataWrapper {
        private CwebData(ResponseData responseData) {
            super(responseData);
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
}
