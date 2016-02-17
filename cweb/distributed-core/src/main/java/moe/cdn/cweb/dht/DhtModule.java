package moe.cdn.cweb.dht;

import com.google.inject.AbstractModule;
import moe.cdn.cweb.dht.annotations.UserMap;
import moe.cdn.cweb.dht.annotations.VoteMap;

public class DhtModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebMap.class).annotatedWith(UserMap.class).to(CwebMapImpl.class);
        bind(CwebMap.class).annotatedWith(VoteMap.class).to(CwebMapImpl.class);
    }
}
