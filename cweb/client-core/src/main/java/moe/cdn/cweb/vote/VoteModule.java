package moe.cdn.cweb.vote;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class VoteModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CwebVoteService.class).to(CwebVoteServiceImpl.class).in(Singleton.class);
    }

}
