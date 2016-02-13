package moe.cdn.cweb.dht;

public interface CwebFuturePut {
    /**
     * Perform and wait for the put operation to complete
     * 
     * @return boolean indicating whether the put was successful or not
     * @throws InterruptedException
     */
    boolean put() throws InterruptedException;
}
