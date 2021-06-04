package com.muchbetter.wallet;

import com.google.inject.AbstractModule;
import com.muchbetter.wallet.block.RedisService;

/**
 * Added to bindingSpec in Application.java
 */
public class WalletModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RedisService.class);
        bind(WalletAction.class);
    }
}
