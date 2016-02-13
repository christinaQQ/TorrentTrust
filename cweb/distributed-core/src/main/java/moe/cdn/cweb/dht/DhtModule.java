package moe.cdn.cweb.dht;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import moe.cdn.cweb.TorrentTrustProtos.SignedUserRecord;
import moe.cdn.cweb.dht.annotations.UserMap;
import moe.cdn.cweb.dht.annotations.VoteMap;
import net.tomp2p.peers.Number160;

public class DhtModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebCollection.class).annotatedWith(UserMap.class).to(CwebCollectionImpl.class);
        bind(CwebCollection.class).annotatedWith(VoteMap.class).to(CwebCollectionImpl.class);
    }

    @Provides
    CwebCollection<SignedUserRecord> provideUserMap() {
        return new CwebCollectionImpl<>(null, Number160.createHash("domain"),
                SignedUserRecord.PARSER);
    }
}
