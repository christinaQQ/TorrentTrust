package moe.cdn.cweb.dht.internal;

import moe.cdn.cweb.dht.internal.tomp2pcompat.PutResponse;
import moe.cdn.cweb.dht.internal.tomp2pcompat.PutResponseWrapper;

/**
 * @author davix
 */
public final class CwebPutResultsImpl extends PutResponseWrapper implements CwebPutResults {
    CwebPutResultsImpl(PutResponse putResponse) {
        super(putResponse);
    }

    @Override
    public boolean ok() {
        return isSuccess();
    }
}
