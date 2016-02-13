package moe.cdn.cweb.dht;

import net.tomp2p.dht.FuturePut;

class CwebFuturePutImpl implements CwebFuturePut {

    private final FuturePut futurePut;

    CwebFuturePutImpl(FuturePut futurePut) {
        this.futurePut = futurePut;
    }

    @Override
    public boolean put() throws InterruptedException {
        return futurePut.awaitUninterruptibly().isSuccess();
    }

}
