package moe.cdn.cweb.dht.util;

import moe.cdn.cweb.security.CwebId;
import net.tomp2p.peers.Number160;

public final class Number160s {
    private Number160s() {}

    public static Number160 fromCwebId(CwebId id) {
        return new Number160(id.toByteArray());
    }
}
