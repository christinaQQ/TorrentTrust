package moe.cdn.cweb.dht;

import com.google.inject.AbstractModule;
import moe.cdn.cweb.dht.annotations.UserDomain;
import moe.cdn.cweb.dht.annotations.VoteDomain;
import net.tomp2p.peers.Number160;

public class DhtModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Number160.class).annotatedWith(UserDomain.class).toInstance(new Number160("user"));
        bind(Number160.class).annotatedWith(VoteDomain.class).toInstance(new Number160("vote"));

        // TODO: create PeerDHT type with validated storage layer

        bind(CwebMap.class).annotatedWith(UserDomain.class).to(CwebMapImpl.class);
        bind(CwebMap.class).annotatedWith(VoteDomain.class).to(CwebMapImpl.class);
    }
}
