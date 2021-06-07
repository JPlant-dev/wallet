package com.muchbetter.wallet;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Names;
import com.muchbetter.wallet.block.RedisService;

/**
 * Added to bindingSpec in Application.java
 */
public class WalletModule extends AbstractModule {

//    private final ArgRetriever argRetriever;
//
//    @Inject
//    public WalletModule(ArgRetriever argRetriever) {
//        this.argRetriever = argRetriever;
//    }

    @Override
    protected void configure() {
//        bind(ArgRetriever.class);
//        bind(String.class).annotatedWith(Names.named(ArgRetriever.HOST_IP_KEY)).toInstance((String)argRetriever.get(ArgRetriever.HOST_IP_KEY));
        bind(RedisService.class);
        bind(WalletAction.class);
    }
}
