package com.muchbetter.wallet;

import com.google.inject.AbstractModule;

public class RedisConfigModule extends AbstractModule {
    private final RedisConfig redisConfig;

    public RedisConfigModule(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Override
    protected void configure() {
        bind(RedisConfig.class).toInstance(redisConfig);
    }
}
